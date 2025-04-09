package com.rideApp.RideApp.services.impl;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.RatingDTO;
import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.DTO.RideStartDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.enums.RideRequestStatus;
import com.rideApp.RideApp.entities.enums.RideStatus;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.exceptions.RuntimeConflictException;
import com.rideApp.RideApp.repository.DriverRepository;
import com.rideApp.RideApp.services.DriverService;
import com.rideApp.RideApp.services.PaymentService;
import com.rideApp.RideApp.services.RatingService;
import com.rideApp.RideApp.services.RideRequestService;
import com.rideApp.RideApp.services.RideService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideDTO acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if (!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)) {
            throw new RuntimeConflictException(
                    "Ride request cannot be accepted , status is " + rideRequest.getRideRequestStatus());
        }
        Driver currentDriver = getCurrentDriver();
        if (!currentDriver.getIsAvailable()) {
            throw new RuntimeConflictException(
                    "Driver cannot accept the ride due to unavailibility ");
        }
        Driver savedDriver = updateDriverAvailibility(currentDriver, false);
        Ride ride = rideService.createNewRide(rideRequest, savedDriver);
        return modelMapper.map(ride, RideDTO.class);

    }

    @Override
    public Driver getCurrentDriver() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("No driver is associated with user with id " + user.getId()));
    }

    @Override
    public Driver updateDriverAvailibility(Driver driver, boolean available) {
        driver.setIsAvailable(available);
        return driverRepository.save(driver);

    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public RideDTO startRide(Long rideId, RideStartDTO rideStartDTO) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();
        if (!driver.equals(ride.getDriver()))
            throw new RuntimeConflictException("Driver cannot start the ride as he has not accepted the ride");

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeConflictException(
                    "Ride status is not CONFIRMED , hence cannot be started , status: " + ride.getRideStatus());
        }

        if (!rideStartDTO.getOtp().equals(ride.getOtp()))
            throw new RuntimeConflictException("OTP is not valid");

        ride.setStartedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ONGOING);

        paymentService.createNewPayment(savedRide);
        ratingService.createNewRating(savedRide);

        return modelMapper.map(savedRide, RideDTO.class);

    }

    @Override
    @Transactional
    public RideDTO endRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();
        if (!driver.equals(ride.getDriver()))
            throw new RuntimeConflictException("Driver cannot end the ride as he has not accepted the ride");

        if (!ride.getRideStatus().equals(RideStatus.ONGOING))
            throw new RuntimeConflictException(
                    "Ride status is not ONGOING , hence cannot be ended , status: " + ride.getRideStatus());

        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ENDED);

        updateDriverAvailibility(driver, true);
        paymentService.processPayment(ride);

        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    @Transactional
    public RideDTO cancelRide(Long rideId) {
        Driver driver = getCurrentDriver();
        Ride ride = rideService.getRideById(rideId);
        if (!driver.equals(ride.getDriver()))
            throw new RuntimeConflictException("Driver cannot cancel the ridee as he has not accepted the ride");

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED))
            throw new RuntimeConflictException(
                    "Ride status is not CONFIRMED , hence cannot be cancelled , status :" + ride.getRideStatus());

        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        updateDriverAvailibility(driver, true);
        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public RiderDTO rateRider(RatingDTO ratingDTO) {
        Ride ride = rideService.getRideById(ratingDTO.getRideId());
        Driver driver = getCurrentDriver();
        if (!driver.equals(ride.getDriver()))
            throw new RuntimeConflictException("Driver is not the owner of this ride");

        if (!ride.getRideStatus().equals(RideStatus.ENDED))
            throw new RuntimeConflictException(
                    "Ride status is not ENDED , hence cannot start rating , status : " + ride.getRideStatus());

        return ratingService.rateRider(ride, ratingDTO.getRating());
    }

    @Override
    public DriverDTO getMyProfile() {

        Driver currentDriver = getCurrentDriver();
        return modelMapper.map(currentDriver, DriverDTO.class);

    }

    @Override
    public Page<RideDTO> getAllMyRides(PageRequest pageRequest) {
        Driver currentDriver = getCurrentDriver();
        Page<Ride> ridePage = rideService.getAllRideOfDriver(currentDriver, pageRequest);
        return ridePage.map(ride -> modelMapper.map(ride, RideDTO.class));
    }

}
