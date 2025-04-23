package com.rideApp.RideApp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.RatingDTO;
import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.DTO.RideRequestDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Rider;
import com.rideApp.RideApp.entities.User;

public interface RiderService {

    RideRequestDTO requestRide(RideRequestDTO rideRequestDTO);

    Rider createNewRider(User user);

    RideDTO cancelRide(Long rideId);

    DriverDTO rateDriver(RatingDTO ratingDTO);

    RiderDTO getMyProfile();

    Page<RideDTO> getAllMyRides(PageRequest pageRequest);

    Double getPricing(RideRequestDTO rideRequestDTO);

}
