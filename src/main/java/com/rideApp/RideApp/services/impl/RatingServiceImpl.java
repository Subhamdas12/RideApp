package com.rideApp.RideApp.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Rating;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.Rider;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.exceptions.RuntimeConflictException;
import com.rideApp.RideApp.repository.DriverRepository;
import com.rideApp.RideApp.repository.RatingRepository;
import com.rideApp.RideApp.repository.RiderRepository;
import com.rideApp.RideApp.services.RatingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;

    @Override
    public Rating createNewRating(Ride ride) {
        Rating rating = Rating.builder()
                .ride(ride)
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .build();

        return rating;
    }

    @Override
    public RiderDTO rateRider(Ride ride, Integer rating) {
        Rider rider = ride.getRider();
        Rating ratingObj = ratingRepository.findByRide(ride).orElseThrow(
                () -> new ResourceNotFoundException("Rating not found for ride with id : " + ride.getId()));
        if (ratingObj.getRiderRating() != null)
            throw new RuntimeConflictException("Rider is already rated, cannot rate again");

        ratingObj.setRiderRating(rating);
        ratingRepository.save(ratingObj);
        Double newRiderRating = ratingRepository.findByRider(rider)
                .stream()
                .mapToDouble(Rating::getRiderRating)
                .average()
                .orElse(0.0);
        rider.setRating(newRiderRating);
        Rider savedRider = riderRepository.save(rider);
        return modelMapper.map(savedRider, RiderDTO.class);
    }

    @Override
    public DriverDTO rateDriver(Ride ride, Integer rating) {

        Driver driver = ride.getDriver();
        Rating ratingObj = ratingRepository.findByRide(ride)
                .orElseThrow(() -> new RuntimeConflictException("Rating not found for ride with id : " + ride.getId()));

        if (ratingObj.getDriverRating() != null)
            throw new RuntimeConflictException("Driver is already rated , cannot rate again");

        ratingObj.setDriverRating(rating);
        ratingRepository.save(ratingObj);
        Double newDriverRating = ratingRepository.findByDriver(driver)
                .stream()
                .mapToDouble(Rating::getDriverRating)
                .average()
                .orElse(0.0);

        driver.setRating(newDriverRating);
        Driver savedDriver = driverRepository.save(driver);
        return modelMapper.map(savedDriver, DriverDTO.class);

    }

}
