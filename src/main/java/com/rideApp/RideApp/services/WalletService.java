package com.rideApp.RideApp.services;

import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.Wallet;
import com.rideApp.RideApp.entities.enums.TransactionMethod;

public interface WalletService {
    Wallet createNewWallet(User user);

    Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride,
            TransactionMethod transactionMethod);

    Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride,
            TransactionMethod transactionMethod);

    Wallet findByUser(User user);
}
