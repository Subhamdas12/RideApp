package com.rideApp.RideApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideApp.RideApp.entities.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

}
