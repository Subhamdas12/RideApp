package com.rideApp.RideApp.services.impl;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideApp.RideApp.DTO.DriverAvailibilityDTO;
import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.DriverLocationDTO;
import com.rideApp.RideApp.DTO.PointDTO;
import com.rideApp.RideApp.DTO.RatingDTO;
import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.DTO.RideStartDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.entities.Rider;
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
import com.rideApp.RideApp.services.UserService;

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
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

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
        RideDTO rideDTO = modelMapper.map(ride, RideDTO.class);
        User riderUser = rideRequest.getRider().getUser();
        messagingTemplate.convertAndSend("/topic/rider/rideAccepted/" + riderUser.getId(), rideDTO);
        return rideDTO;

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
        RideDTO rideDTO = modelMapper.map(savedRide, RideDTO.class);
        User riderUser = ride.getRider().getUser();
        messagingTemplate.convertAndSend("/topic/rider/rideStart/" + riderUser.getId(), rideDTO);
        return rideDTO;

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
        User riderUser = ride.getRider().getUser();
        RideDTO rideDTO = modelMapper.map(savedRide, RideDTO.class);
        messagingTemplate.convertAndSend("/topic/rider/rideEnd/" + riderUser.getId(), rideDTO);
        return rideDTO;
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
        RideDTO rideDTO = modelMapper.map(savedRide, RideDTO.class);
        User riderUser = ride.getRider().getUser();
        messagingTemplate.convertAndSend("/topic/rider/rideCancel/" + riderUser.getId(), rideDTO);
        return rideDTO;
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

    @Override
    public DriverDTO setDriverAvailibility(DriverAvailibilityDTO driverAvailibilityDTO) {
        boolean isAvailable = driverAvailibilityDTO.getIsAvailable();
        Driver currentDriver = getCurrentDriver();
        return modelMapper.map(updateDriverAvailibility(currentDriver, isAvailable), DriverDTO.class);
    }

    @Override
    public DriverDTO setDriverLocation(DriverLocationDTO driverLocationDTO) {
        User currentUser = userService.getUserById(driverLocationDTO.getUserId());
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("No driver found with userId " + currentUser.getId()));
        PointDTO pointDTO = new PointDTO(driverLocationDTO.getCoordinates());
        Point point = modelMapper.map(pointDTO, Point.class);
        driver.setCurrentLocation(point);
        Driver savedDriver = driverRepository.save(driver);
        return modelMapper.map(savedDriver, DriverDTO.class);

    }

}
