package com.project.uber.uberApp.services;


import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RiderDto;

public interface RatingService {

    DriverDto RateDriver(Ride ride, Integer rating);

    RiderDto RateRider(Ride ride , Integer rating);
}
