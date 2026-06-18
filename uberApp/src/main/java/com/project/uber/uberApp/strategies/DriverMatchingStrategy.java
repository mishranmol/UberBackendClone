package com.project.uber.uberApp.strategies;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.dto.RideRequestDto;

import java.util.List;

public interface DriverMatchingStrategy {


    List<Driver> findMatchingDriver(RideRequest rideRequest);

}
