package com.rideApp.RideApp.DTO;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.rideApp.RideApp.entities.Rider;
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

    private Point pickupLocation;

    private Point dropoffLocation;

    private LocalDateTime requestedTime;

    private Rider rider;
    private PaymentMethod paymentMethod;
    private RideRequestStatus rideRequestStatus;
    private Double fare;
}
