package com.project.uber.uberApp.strategies;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.dto.RideRequestDto;

import java.util.List;

public interface DriverMatchingStrategy {

    // We were passing "RideRequestDto" but now we are passing "RideRequest" because it contains more info. than Dto and since it's an
    // internal method so we can pass it.
    List<Driver> findMatchingDriver(RideRequest rideRequest);

}
