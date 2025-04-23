package com.rideApp.RideApp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.rideApp.RideApp.DTO.DriverAvailibilityDTO;
import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.DriverLocationDTO;
import com.rideApp.RideApp.DTO.RatingDTO;
import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.DTO.RideStartDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Driver;

public interface DriverService {

    RideDTO acceptRide(Long rideRequestId);

    Driver getCurrentDriver();

    Driver updateDriverAvailibility(Driver driver, boolean available);

    Driver createNewDriver(Driver driver);

    RideDTO startRide(Long rideId, RideStartDTO rideStartDTO);

    RideDTO endRide(Long rideId);

    RideDTO cancelRide(Long rideId);

    RiderDTO rateRider(RatingDTO ratingDTO);

    DriverDTO getMyProfile();

    Page<RideDTO> getAllMyRides(PageRequest pageRequest);

    DriverDTO setDriverAvailibility(DriverAvailibilityDTO driverAvailibilityDTO);

    DriverDTO setDriverLocation(DriverLocationDTO driverLocationDTO);
}
