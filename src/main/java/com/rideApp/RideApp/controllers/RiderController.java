package com.rideApp.RideApp.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.RatingDTO;
import com.rideApp.RideApp.DTO.RideDTO;
import com.rideApp.RideApp.DTO.RideRequestDTO;
import com.rideApp.RideApp.DTO.RiderDTO;
import com.rideApp.RideApp.DTO.UserDTO;
import com.rideApp.RideApp.services.RiderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {
    private final RiderService riderService;

    @PostMapping("/requestRide")
    public ResponseEntity<RideRequestDTO> requestRide(@RequestBody RideRequestDTO rideRequestDTO) {
        return new ResponseEntity<>(riderService.requestRide(rideRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDTO> cancelRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(riderService.cancelRide(rideId));
    }

    @PostMapping("/rateDriver")
    public ResponseEntity<DriverDTO> rateDriver(@RequestBody RatingDTO ratingDTO) {
        return ResponseEntity.ok(riderService.rateDriver(ratingDTO));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<RiderDTO> getMyProfile() {
        return ResponseEntity.ok(riderService.getMyProfile());
    }

    @PostMapping("/getPricing")
    public ResponseEntity<Double> getPricing(@RequestBody RideRequestDTO rideRequestDTO) {
        return ResponseEntity.ok(riderService.getPricing(rideRequestDTO));
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDTO>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageOffset,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize,
                Sort.by(Sort.Direction.DESC, "createdTime", "id"));
        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }

}
