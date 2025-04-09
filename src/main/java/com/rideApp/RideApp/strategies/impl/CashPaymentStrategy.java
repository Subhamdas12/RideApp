package com.rideApp.RideApp.strategies.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.Payment;
import com.rideApp.RideApp.entities.enums.PaymentStatus;
import com.rideApp.RideApp.entities.enums.TransactionMethod;
import com.rideApp.RideApp.repository.PaymentRepository;
import com.rideApp.RideApp.services.WalletService;
import com.rideApp.RideApp.strategies.PaymentStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {
    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        double platformCommission = payment.getAmount() * PLATFORM_COMMISION;
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission, null, payment.getRide(),
                TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }

}
