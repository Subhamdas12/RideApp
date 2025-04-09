package com.rideApp.RideApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Rating;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.Rider;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByRide(Ride ride);

    List<Rating> findByRider(Rider rider);

    List<Rating> findByDriver(Driver driver);

}
