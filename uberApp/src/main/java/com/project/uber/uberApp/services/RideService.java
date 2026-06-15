package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.enums.RideStatus;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

//It deals with all things related to Ride
//This is used so our Code Remains DRY only & it can use all below methods
public interface RideService {

    Ride getRideById(Long rideId) throws ResourceNotFoundException;

    Ride createNewRide(RideRequest rideRequest , Driver driver);

    // Rider/Driver can call this method and update the status of the Ride
    Ride updateRideStatus(Ride ride, RideStatus rideStatus);

    Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest);

    //This returns the data in Page Format , PageRequest contains the PageNo. and no. of Data user wants.
    //A driver can have thousands of ride , so we won't send all rides in one go so hence using PageRequest
    Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest);


}
