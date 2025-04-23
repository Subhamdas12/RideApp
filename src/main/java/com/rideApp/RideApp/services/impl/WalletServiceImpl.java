package com.rideApp.RideApp.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.rideApp.RideApp.DTO.WalletDTO;
import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.Wallet;
import com.rideApp.RideApp.entities.WalletTransaction;
import com.rideApp.RideApp.entities.enums.TransactionMethod;
import com.rideApp.RideApp.entities.enums.TransactionType;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.repository.WalletRepository;
import com.rideApp.RideApp.services.UserService;
import com.rideApp.RideApp.services.WalletService;
import com.rideApp.RideApp.services.WalletTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride,
            TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance() + amount);

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amout(amount)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .ride(ride)
                .wallet(wallet)
                .build();

        walletTransactionService.createNewWalletTransaction(walletTransaction);

        return walletRepository.save(wallet);

    }

    @Override
    public Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride,
            TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance() - amount);

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amout(amount)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .ride(ride)
                .wallet(wallet)
                .build();

        walletTransactionService.createNewWalletTransaction(walletTransaction);

        return wallet;
    }

    @Override
    public Wallet findByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No wallet found with user with id " + user.getId()));
    }

    @Override
    public WalletDTO getMyWallet() {
        User user = userService.getCurrentUser();
        Wallet wallet = findByUser(user);
        return modelMapper.map(wallet, WalletDTO.class);

    }

}
