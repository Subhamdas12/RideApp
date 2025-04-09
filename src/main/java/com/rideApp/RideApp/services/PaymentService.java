package com.rideApp.RideApp.services;

import com.rideApp.RideApp.entities.Payment;
import com.rideApp.RideApp.entities.Ride;

public interface PaymentService {
    Payment createNewPayment(Ride ride);

    void processPayment(Ride ride);
}
