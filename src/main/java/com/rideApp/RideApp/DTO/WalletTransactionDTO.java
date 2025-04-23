package com.rideApp.RideApp.DTO;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.rideApp.RideApp.entities.Ride;
import com.rideApp.RideApp.entities.enums.TransactionMethod;
import com.rideApp.RideApp.entities.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionDTO {

    private Long id;

    private Double amout;
    private TransactionType transactionType;
    private TransactionMethod transactionMethod;

    private RideDTO ride;

    // private WalletDTO wallet;

    private LocalDateTime timeStamp;
}
