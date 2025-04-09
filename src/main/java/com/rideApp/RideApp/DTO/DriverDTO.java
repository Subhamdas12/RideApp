package com.rideApp.RideApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private Long id;
    private UserDTO user;
    private Boolean isAvailable;
    private Double rating;
    private String vehicleId;
}
