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


    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;
    private final RatingRepository ratingRepository;

    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {

        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            throw new RuntimeException("RideRequest cannot be accepted,status is : "+rideRequest.getRideRequestStatus());
        }

        Driver currentDriver = getCurrentDriver();

        if(!currentDriver.getAvailable()) {
           throw new RuntimeException("Driver cannot accept due to Unavailability");
        }


        Driver savedDriver = updateDriverAvailability(currentDriver,false);

        Ride acceptedRide = rideService.createNewRide(rideRequest,savedDriver);

        return modelMapper.map(acceptedRide,RideDto.class);
    }


    @Override
    @Transactional
    public RideDto startRide(Long rideId, String otp) {


        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();


        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start this Ride as he has not accepted this ride earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride status is not Confirmed hence cannot be started, current status : " +ride.getRideStatus());
        }

        if(!otp.equals(ride.getOtp())){
            throw new RuntimeException("Otp is not Valid, otp: "+otp);
        }


        ride.setStartedAt(LocalDateTime.now());

        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ONGOING);

       Rating rating = Rating.builder()
                .ride(ride)
                .rider(ride.getRider())
                .driver(driver)
                .build();
        ratingRepository.save(rating);

        paymentService.createNewPayment(savedRide);

        return modelMapper.map(savedRide,RideDto.class);

    }


    @Override
    @Transactional
    public RideDto cancelRide(Long rideId) {

        Ride ride = rideService.getRideById(rideId);

        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.equals(ride.getDriver())){
            throw new RuntimeException("This Driver cannot cancel this Ride as he doesn't owns this ride: "+rideId);
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride Cannot be Cancelled , invalid Status "+ride.getRideStatus());
        }

        rideService.updateRideStatus(ride,RideStatus.CANCELLED);

        Driver savedDriver = updateDriverAvailability(currentDriver,true);

        return modelMapper.map(ride,RideDto.class);

    }


    @Override
    @Transactional
    public RideDto endRide(Long rideId) {

        Ride ride = rideService.getRideById(rideId);

        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.equals(ride.getDriver())){
            throw new RuntimeException("This Driver cannot cancel this Ride as he doesn't owns this ride: "+rideId);
        }

        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RuntimeException("Ride Status is not ONGOING , hence cannot be ended: "+ride.getRideStatus());
        }

        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ENDED);


        Driver savedDriver = updateDriverAvailability(currentDriver,true);


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


        if(!ride.getDriver().equals(currentDriver)){
            throw new RuntimeException("Cannot give the rating as Driver doesn't own's this ride"+currentDriver);
        }

        //If the ride is not ENDED then driver cannot ride the customer/Rider.
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Driver cannot rate the Rider as Ride is not yet Ended"+ride.getRideStatus());
        }

        return ratingService.RateRider(ride,rating);

    }


    @Override
    public DriverDto getMyProfile() {
       Driver Currentdriver = getCurrentDriver();
       return modelMapper.map(Currentdriver,DriverDto.class);
    }


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
