package com.rideApp.RideApp.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.rideApp.RideApp.DTO.DriverLocationDTO;
import com.rideApp.RideApp.services.DriverService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DriverLocationController {
    private final DriverService driverService;

    @MessageMapping("/driver/location")
    public void receiveDriverLocation(DriverLocationDTO driverLocationDTO) {

        driverService.setDriverLocation(driverLocationDTO);

    }

}
