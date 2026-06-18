package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;


public interface DriverService {


    RideDto acceptRide(Long rideRequestId);

    RideDto startRide(Long rideId,String otp) throws ResourceNotFoundException;


    RideDto cancelRide(Long rideId) throws ResourceNotFoundException;

    RideDto endRide(Long rideId) throws ResourceNotFoundException;


    Driver updateDriverAvailability(Driver driver, boolean available);


   RiderDto rateRider(Long rideId, Integer rating) throws ResourceNotFoundException;


   DriverDto getMyProfile();


   Page<RideDto> getAllMyRides(PageRequest pageRequest);

   Driver getCurrentDriver() throws ResourceNotFoundException;

}
