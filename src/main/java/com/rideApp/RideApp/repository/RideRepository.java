package com.rideApp.RideApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.Rider;

public interface RideRepository extends JpaRepository<Ride, Long> {

    Page<Ride> findByDriver(Driver driver, PageRequest pageRequest);

    Page<Ride> findByRider(Rider rider, PageRequest pageRequest);

}
