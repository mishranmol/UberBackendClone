package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.enums.RideRequestStatus;
import com.project.uber.uberApp.Entities.enums.RideStatus;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRepository;
import com.project.uber.uberApp.repositories.RiderRepository;
import com.project.uber.uberApp.services.RideRequestService;
import com.project.uber.uberApp.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor//This will create a Constructor on our behalf , we don't have to create it manually then.
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId).
                orElseThrow(() -> new ResourceNotFoundException("Ride with the given Id not found: "+rideId));
    }


    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {

        //Set the status as CONFIRMED as driver accepted the rideRequest
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);

        //Since mostly fields b/t rideRequest & Ride are Similar so using ModelMapper to convert rideRequest into Ride
       Ride ride = modelMapper.map(rideRequest,Ride.class);
       ride.setRideStatus(RideStatus.CONFIRMED);
       ride.setDriver(driver);
       ride.setOtp(generateRandomOTP());
       ride.setId(null); //Don't know the logic behind this

        //
        rideRequestService.update(rideRequest);

       //Saving Ride inside our DB
       return rideRepository.save(ride);

    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {
        return rideRepository.findByRider(rider, pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return rideRepository.findByDriver(driver, pageRequest);
    }


    //Going to generate 4 Digit Random OTP
    private String generateRandomOTP(){
        Random random = new Random();
        int otp = random.nextInt(10000); //It will give us Random Number b/t 0->9999.
        return String.format("%04d",otp); //String.format("%04d", otp) is used to convert a number into a 4-digit string, adding leading zeros
        // if necessary. Example : int otp = 7; String result = String.format("%04d", otp); Output : 0007

    }
}
