package com.rideApp.RideApp.strategies;

import java.util.List;

import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.RideRequest;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDriver(RideRequest rideRequest);
}
