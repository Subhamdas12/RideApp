package com.rideApp.RideApp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideApp.RideApp.DTO.WalletDTO;
import com.rideApp.RideApp.services.WalletService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/wallet")
@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/getMyWallet")
    public ResponseEntity<WalletDTO> getMyWallet() {
        return ResponseEntity.ok(walletService.getMyWallet());
    }

}
