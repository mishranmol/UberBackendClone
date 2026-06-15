package com.project.uber.uberApp.services;


import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RiderDto;

public interface RatingService {

    //For this particular Ride , for this driver give this Rating
    DriverDto RateDriver(Ride ride, Integer rating);

    //We should create a Rating Object As soon as we start the Ride.
    RiderDto RateRider(Ride ride , Integer rating);
}
