package com.rideApp.RideApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverLocationDTO {
    private Long userId;
    private double[] coordinates;
}
