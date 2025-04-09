package com.rideApp.RideApp.services.impl;

import org.springframework.stereotype.Service;

import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.repository.RideRequestRepository;
import com.rideApp.RideApp.services.RideRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {
    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride request not found with id " + rideRequestId));
    }

    @Override
    public void update(RideRequest rideRequest) {
        rideRequestRepository.findById(rideRequest.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Ride request not found with id: " + rideRequest.getId()));
        rideRequestRepository.save(rideRequest);
    }

}
