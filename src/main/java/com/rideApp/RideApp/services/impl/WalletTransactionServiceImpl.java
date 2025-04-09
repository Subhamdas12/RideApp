package com.rideApp.RideApp.services.impl;

import org.springframework.stereotype.Service;

import com.rideApp.RideApp.entities.WalletTransaction;
import com.rideApp.RideApp.repository.WalletRepository;
import com.rideApp.RideApp.repository.WalletTransactionRepository;
import com.rideApp.RideApp.services.WalletTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public WalletTransaction createNewWalletTransaction(WalletTransaction walletTransaction) {
        return walletTransactionRepository.save(walletTransaction);
    }

}
