package com.project.uber.uberApp.strategies.impl;

import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.services.DistanceService;
import com.project.uber.uberApp.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {

    //SURGE means "sudden large increase".
    private final DistanceService distanceService;

    // This SURGE_FACTOR can depend on lot of factors in real world Scenario like if raining then this factor can be different ,
    // we can call an Free weather API and this SURGE_FACTOR would be different then.
    private static final double SURGE_FACTOR = 2 ;

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance = distanceService.calculateDistance(rideRequest.getPickupLocation(),rideRequest.getDropOffLocation());
        return distance * SURGE_FACTOR * 10;
    }
}
