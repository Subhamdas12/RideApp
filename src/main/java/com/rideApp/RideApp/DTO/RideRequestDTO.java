package com.rideApp.RideApp.DTO;

import java.time.LocalDateTime;

import com.rideApp.RideApp.entities.enums.PaymentMethod;
import com.rideApp.RideApp.entities.enums.RideRequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDTO {
    private Long id;

    private PointDTO pickupLocation;

    private PointDTO dropoffLocation;

    private LocalDateTime requestedTime;

    private RiderDTO rider;
    private PaymentMethod paymentMethod;
    private RideRequestStatus rideRequestStatus;
    private Double fare;
}
