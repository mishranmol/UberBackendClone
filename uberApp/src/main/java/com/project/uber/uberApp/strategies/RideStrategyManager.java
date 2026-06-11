package com.project.uber.uberApp.strategies;

import com.project.uber.uberApp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.project.uber.uberApp.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.project.uber.uberApp.strategies.impl.RideFareDefaultFareCalculationStrategy;
import com.project.uber.uberApp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//StrategyManager responsibility is to give you which strategy to use DriverMatchingStrategy & RideFareCalculationStrategy
@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy driverMatchingHighestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy driverMatchingNearestDriverStrategy;
    private final RideFareDefaultFareCalculationStrategy rideFareDefaultFareCalculationStrategy;
    private final RideFareSurgePricingFareCalculationStrategy rideFareSurgePricingFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating){
        if(riderRating >= 4.8){
            return driverMatchingHighestRatedDriverStrategy;
        }else {
            return driverMatchingNearestDriverStrategy;
        }
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy(){

        //SURGE means "sudden large increase"
        //Suppose the SurgeTime is Between 6PM to 9PM
        LocalTime surgeStartTime = LocalTime.of(18,0);
        LocalTime surgeEndTime = LocalTime.of(21,0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);

        if(isSurgeTime){
            return rideFareSurgePricingFareCalculationStrategy;
        }else{
            return rideFareDefaultFareCalculationStrategy;
        }
    }
}
