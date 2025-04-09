package com.rideApp.RideApp.services;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Rating;
import com.rideApp.RideApp.entities.Ride;

public interface RatingService {
    Rating createNewRating(Ride ride);

    RiderDTO rateRider(Ride ride, Integer rating);

    DriverDTO rateDriver(Ride ride, Integer rating);
}
