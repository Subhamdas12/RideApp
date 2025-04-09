package com.rideApp.RideApp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.entities.Rider;
import com.rideApp.RideApp.entities.enums.RideStatus;

public interface RideService {

    Ride createNewRide(RideRequest rideRequest, Driver driver);

    Ride getRideById(Long rideId);

    Ride updateRideStatus(Ride ride, RideStatus rideStatus);

    Page<Ride> getAllRideOfRider(Rider rider, PageRequest pageRequest);

    Page<Ride> getAllRideOfDriver(Driver driver, PageRequest pageRequest);
}
