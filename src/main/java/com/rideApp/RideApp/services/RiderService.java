package com.rideApp.RideApp.services;

import org.springframework.stereotype.Service;

import com.rideApp.RideApp.DTO.RideRequestDTO;

public interface RiderService {

    public RideRequestDTO requestRide(RideRequestDTO rideRequestDTO);

}
