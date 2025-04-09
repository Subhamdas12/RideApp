package com.rideApp.RideApp.services.impl;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.RatingDTO;
import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.DTO.RideRequestDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.entities.Rider;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.enums.RideRequestStatus;
import com.rideApp.RideApp.entities.enums.RideStatus;
import com.rideApp.RideApp.entities.enums.Role;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.exceptions.RuntimeConflictException;
import com.rideApp.RideApp.repository.RideRequestRepository;
import com.rideApp.RideApp.repository.RiderRepository;
import com.rideApp.RideApp.services.DriverService;
import com.rideApp.RideApp.services.RatingService;
import com.rideApp.RideApp.services.RideService;
import com.rideApp.RideApp.services.RiderService;
import com.rideApp.RideApp.strategies.RideStrategyManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RiderRepository riderRepository;
    private final RideRequestRepository rideRequestRepository;
    private final RideStrategyManager rideStrategyManager;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;

    @Override
    public RideRequestDTO requestRide(RideRequestDTO rideRequestDTO) {
        Rider rider = getCurrentRider();
        RideRequest rideRequest = modelMapper.map(rideRequestDTO, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);

        double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);
        List<Driver> drivers = rideStrategyManager.driverMatchingStrategy(rider.getRating())
                .findMatchingDriver(savedRideRequest);

        return modelMapper.map(savedRideRequest, RideRequestDTO.class);
    }

    private Rider getCurrentRider() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return riderRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Rider not associated with user id : " + user.getId()));
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .rating(0.0)
                .user(user)
                .build();

        return riderRepository.save(rider);
    }

    @Override
    public RideDTO cancelRide(Long rideId) {
        Rider rider = getCurrentRider();
        Ride ride = rideService.getRideById(rideId);
        if (!rider.equals(ride.getRider()))
            throw new RuntimeConflictException("Rider doesnot own the ride with id " + rideId);

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeConflictException("Ride cannot be cancelled , invalid status: " + ride.getRideStatus());
        }

        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        driverService.updateDriverAvailibility(ride.getDriver(), true);
        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public DriverDTO rateDriver(RatingDTO ratingDTO) {
        Ride ride = rideService.getRideById(ratingDTO.getRideId());
        Rider rider = getCurrentRider();
        if (!rider.equals(ride.getRider()))
            throw new RuntimeConflictException("Rider is not the owner of the ride");
        if (!ride.getRideStatus().equals(RideStatus.ENDED))
            throw new RuntimeConflictException(
                    "Ride status is not ENDED , hence cannot start rating , status : " + ride.getRideStatus());

        return ratingService.rateDriver(ride, ratingDTO.getRating());
    }

    @Override
    public RiderDTO getMyProfile() {
        Rider currentRider = getCurrentRider();
        return modelMapper.map(currentRider, RiderDTO.class);
    }

    @Override
    public Page<RideDTO> getAllMyRides(PageRequest pageRequest) {
        Rider currentRider = getCurrentRider();
        Page<Ride> ridePage = rideService.getAllRideOfRider(currentRider, pageRequest);
        return ridePage.map(ride -> modelMapper.map(ride, RideDTO.class));

    }

}
