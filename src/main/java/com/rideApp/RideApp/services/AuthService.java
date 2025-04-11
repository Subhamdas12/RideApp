package com.rideApp.RideApp.services;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.LoginRequestDTO;
import com.rideApp.RideApp.DTO.LoginResponseDTO;
import com.rideApp.RideApp.DTO.OnBoardNewDriverDTO;
import com.rideApp.RideApp.DTO.SignupDTO;
import com.rideApp.RideApp.DTO.UserDTO;

public interface AuthService {
    LoginResponseDTO signup(SignupDTO signupDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    DriverDTO onBoardNewDriver(Long userId, OnBoardNewDriverDTO onBoardNewDriverDTO);

    LoginResponseDTO refreshAccessToken(String refreshToken);

    void logout(String refreshToken);
}
