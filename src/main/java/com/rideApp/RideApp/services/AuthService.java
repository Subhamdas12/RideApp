package com.rideApp.RideApp.services;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.LoginRequestDTO;
import com.rideApp.RideApp.DTO.OnBoardNewDriverDTO;
import com.rideApp.RideApp.DTO.SignupDTO;
import com.rideApp.RideApp.DTO.UserDTO;

public interface AuthService {
    UserDTO signup(SignupDTO signupDTO);

    String[] login(LoginRequestDTO loginRequestDTO);

    DriverDTO onBoardNewDriver(Long userId, OnBoardNewDriverDTO onBoardNewDriverDTO);
}
