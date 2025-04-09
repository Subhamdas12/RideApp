package com.rideApp.RideApp.strategies;

import com.rideApp.RideApp.entities.Payment;

public interface PaymentStrategy {
    Double PLATFORM_COMMISION = 0.3;

    void processPayment(Payment payment);
}
