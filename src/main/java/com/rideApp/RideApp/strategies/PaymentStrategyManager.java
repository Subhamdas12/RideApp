package com.rideApp.RideApp.strategies;

import org.springframework.stereotype.Component;

import com.rideApp.RideApp.entities.enums.PaymentMethod;
import com.rideApp.RideApp.strategies.impl.CashPaymentStrategy;
import com.rideApp.RideApp.strategies.impl.WalletPaymentStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final WalletPaymentStrategy walletPaymentStrategy;
    private final CashPaymentStrategy cashPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
        };
    }

}
