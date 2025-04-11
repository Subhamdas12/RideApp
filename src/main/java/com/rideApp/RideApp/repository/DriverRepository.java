package com.rideApp.RideApp.repository;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.User;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
        Optional<Driver> findByUser(User user);

        @Query(value = "SELECT d.* " +
                        "FROM driver d " +
                        "WHERE d.is_available = true AND ST_DWithin(d.current_location,:pickupLocation,15000) " +
                        "ORDER BY d.rating DESC " +
                        "LIMIT 10", nativeQuery = true)
        List<Driver> findTenNearbyTopRatedDrivers(Point pickupLocation);

        @Query(value = "SELECT d.*, ST_Distance(d.current_location,:pickupLocation) AS distance " +
                        "FROM driver d " +
                        "WHERE d.is_available = true AND ST_DWithin(d.current_location,:pickupLocation,10000) " +
                        "ORDER BY distance " +
                        "LIMIT 10", nativeQuery = true)
        List<Driver> findTenNearestDrivers(Point pickupLocation);
}
