package com.rideApp.RideApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);

}