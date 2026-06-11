package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RiderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

//It deals with all things related to Driver
public interface DriverService {


    RideDto acceptRide(Long rideRequestId);

    RideDto startRide(Long rideId,String otp);

    // Need rideId to cancel a Ride and driverId which will get from SpringSecurity ContextHolder , so if this ride belongs
    // to this particular Driver then cancel this ride.
    // My cancelRide Logic -> A driver can only cancel a ride when the ride has not yet "started" and somehow accidentally you have "accepted" the ride
    //then driver can cancel the ride , But once ride has started(i.e->ONGOING State) then you can't cancel the ride , you can "end" the ride only.
    RideDto cancelRide(Long rideId);

    RideDto endRide(Long rideId);

    //Updates the driver status of whether available or not.
    Driver updateDriverAvailability(Driver driver, boolean available);

    //For rating a rider we need rating as well
   RiderDto rateRider(Long rideId, Integer rating);

   //driverId which will get from SpringSecurity ContextHolder
   DriverDto getMyProfile();

    // This returns the data in Page Format , PageRequest contains the PageNo. and no. of Data user wants.
    // A driver can have thousands of ride , so we won't send all rides in one go so hence using PageRequest.
   Page<RideDto> getAllMyRides(PageRequest pageRequest);

   Driver getCurrentDriver();

}
