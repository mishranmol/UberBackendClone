package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.*;
import com.project.uber.uberApp.Entities.enums.RideRequestStatus;
import com.project.uber.uberApp.Entities.enums.RideStatus;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.repositories.RatingRepository;
import com.project.uber.uberApp.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    //A very common concurrency issue in an Uber-like application is multiple drivers accepting the same ride request at the same time.
    //Note -> To handle concurrency issue we can use MessageQueues(i.e->RabbitMQ/Kafka).

    //ConcurrentMap may work if there is only 1 server and multiple Threads , but for
    //multiple servers like 1 request coming to one server and another request to another server then ConcurrentMap won't work.


    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;
    private final RatingRepository ratingRepository;

    @Override
    @Transactional // To maintain "Atomicity" -> Either everything will execute or nothing will execute.
    public RideDto acceptRide(Long rideRequestId) {

        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        //If it's NOT PENDING
        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            throw new RuntimeException("RideRequest cannot be accepted,status is : "+rideRequest.getRideRequestStatus());
        }

        Driver currentDriver = getCurrentDriver();

        if(!currentDriver.getAvailable()) {
           throw new RuntimeException("Driver cannot accept due to Unavailability");
        }

        //Since driver has accepted the Ride so mark the availability of this driver as "false".
        Driver savedDriver = updateDriverAvailability(currentDriver,false);


        //Since the rideRequest is still "PENDING" & the currentDriver is also "Available" means we can create a NewRide now.
        Ride acceptedRide = rideService.createNewRide(rideRequest,savedDriver);

        return modelMapper.map(acceptedRide,RideDto.class);
    }

    //To start a ride we need OTP as well Along with RideId.
    @Override
    @Transactional
    public RideDto startRide(Long rideId, String otp) {

        //Checking if the Driver owns this particular Ride or not.
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        //If driver doesn't own's this Ride
        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start this Ride as he has not accepted this ride earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride status is not Confirmed hence cannot be started, current status : " +ride.getRideStatus());
        }

        if(!otp.equals(ride.getOtp())){
            throw new RuntimeException("Otp is not Valid, otp: "+otp);
        }

        //Setting the StartTime of the ride
        ride.setStartedAt(LocalDateTime.now());

        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ONGOING);

        //As soon as Driver Starts the ride creating a "Rating" Object , so that while giving rating either to Rider OR Driver we can get this
        //Rating Object for this particular Ride.
       Rating rating = Rating.builder()
                .ride(ride)
                .rider(ride.getRider())
                .driver(driver)
                .build();
        ratingRepository.save(rating);

        //As soon ride is getting Started , creating a Payment Object that is Associated with this Ride.
        paymentService.createNewPayment(savedRide);

        return modelMapper.map(savedRide,RideDto.class);

    }


    @Override
    @Transactional
    public RideDto cancelRide(Long rideId) {

        Ride ride = rideService.getRideById(rideId);

        //Checking if the driver own's this ride or not
        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.equals(ride.getDriver())){
            //We can throw UNAUTHORIZED Exception below as well , TODO -> In SPRINGSECURITY
            throw new RuntimeException("This Driver cannot cancel this Ride as he doesn't owns this ride: "+rideId);
        }

        //If rideStatus is "CONFIRMED" then driver can "CANCEL" otherwise if any other state(i.e->ONGOING,CANCELLED,ENDED) then cannot "CANCEL".
        //Means in short if the ride is in "ONGOING" state then driver cannot cancel.
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride Cannot be Cancelled , invalid Status "+ride.getRideStatus());
        }

        rideService.updateRideStatus(ride,RideStatus.CANCELLED);

        //Since driver has cancelled the ride , so now make driver available.
        Driver savedDriver = updateDriverAvailability(currentDriver,true);

        return modelMapper.map(ride,RideDto.class);

    }


    @Override
    @Transactional
    public RideDto endRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);

        //Checking if the driver owns this ride or not
        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.equals(ride.getDriver())){
            throw new RuntimeException("This Driver cannot cancel this Ride as he doesn't owns this ride: "+rideId);
        }

        //If rideStatus is "ONGOING" then driver can "END" otherwise if any other state(i.e->CONFIRMED,CANCELLED,ENDED) then cannot "END".
        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RuntimeException("Ride Status is not ONGOING , hence cannot be ended: "+ride.getRideStatus());
        }

        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ENDED);//We are saving the Ride in DB while doing the "updateRideStatus"
        //so before that set the EndedRideTime as well so that it gets saved inside DB.

        //Since driver has Ended the ride , so now make driver available.
        Driver savedDriver = updateDriverAvailability(currentDriver,true);

        //As ride gets Ended , process the payment for this particular Ride.
        paymentService.processPayment(savedRide);

        return modelMapper.map(savedRide,RideDto.class);


    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {
        driver.setAvailable(available);
        return driverRepository.save(driver);
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {

        Ride ride = rideService.getRideById(rideId);
        Driver currentDriver = getCurrentDriver();

        //Checking if the currentDriver owns this ride or not , if not then Driver cannot rate this Rider.
        if(!ride.getDriver().equals(currentDriver)){
            throw new RuntimeException("Cannot give the rating as Driver doesn't own's this ride"+currentDriver);
        }

        //If the ride is not ENDED then driver cannot ride the customer/Rider.
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Driver cannot rate the Rider as Ride is not yet Ended"+ride.getRideStatus());
        }

        return ratingService.RateRider(ride,rating);

    }

    //Ques -> So here we are returning "DriverDTO" , but it also has a "UserDto" and other details so how Modelmapper will map UserDto with UserEntity?
    //Ans -> ModelMapper work recursively , it will go inside DriverDto and maps all things and while mapping it will map "UserDto" with User Entity as well.
    @Override
    public DriverDto getMyProfile() {
       Driver Currentdriver = getCurrentDriver();
       return modelMapper.map(Currentdriver,DriverDto.class);
    }


    //map method is from Page
    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
        Driver currentDriver = getCurrentDriver();
        return rideService.getAllRidesOfDriver(currentDriver,pageRequest).
                map(ride -> modelMapper.map(ride,RideDto.class));
    }

    @Override
    //TODO -> IMPLEMENTATION USING SPRING SECURITY
    public Driver getCurrentDriver() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return driverRepository.findByUser(user).orElse(null);
        }
}
