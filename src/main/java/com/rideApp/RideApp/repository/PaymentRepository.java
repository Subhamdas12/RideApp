package com.rideApp.RideApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideApp.RideApp.entities.Payment;
import com.rideApp.RideApp.entities.Ride;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRide(Ride ride);

}
