package com.rideApp.RideApp.strategies;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.rideApp.RideApp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.rideApp.RideApp.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.rideApp.RideApp.strategies.impl.RideFareDefaultFareCalculationStrategy;
import com.rideApp.RideApp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy driverMatchingHighestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy driverMatchingNearestDriverStrategy;
    private final RideFareDefaultFareCalculationStrategy rideFareDefaultFareCalculationStrategy;
    private final RideFareSurgePricingFareCalculationStrategy rideFareSurgePricingFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating) {
        if (riderRating >= 4.5) {
            return driverMatchingHighestRatedDriverStrategy;
        } else {
            return driverMatchingNearestDriverStrategy;
        }
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy() {
        LocalTime surgeStarTime = LocalTime.of(18, 0);
        LocalTime surgeEndTime = LocalTime.of(21, 0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStarTime) && currentTime.isBefore(surgeEndTime);

        if (isSurgeTime) {
            return rideFareSurgePricingFareCalculationStrategy;
        } else {
            return rideFareDefaultFareCalculationStrategy;
        }
    }
}
