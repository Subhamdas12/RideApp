package com.rideApp.RideApp.strategies.impl;

import org.springframework.stereotype.Service;

import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.strategies.DistanceService;
import com.rideApp.RideApp.strategies.RideFareCalculationStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {

    public final double SURGE_FACTORY = 2;
    private final DistanceService distanceService;

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance = distanceService.calculateDistance(rideRequest.getPickupLocation(),
                rideRequest.getDropoffLocation());
        return distance * RIDE_FARE_MULTIPLIER * SURGE_FACTORY;
    }

}