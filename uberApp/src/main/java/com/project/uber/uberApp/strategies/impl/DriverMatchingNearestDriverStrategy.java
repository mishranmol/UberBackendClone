package com.project.uber.uberApp.strategies.impl;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//All the business logic needs to be inside Service
@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;
    @Override
    public List<Driver> findMatchingDriver(RideRequest rideRequest) {
        //We'll be creating custom method inside the Repository that will have custom Query which will work on our Geo-Spatial DB
        //Only pass the Pickup location
        return driverRepository.findTenNearestDrivers(rideRequest.getPickupLocation());

    }
}
