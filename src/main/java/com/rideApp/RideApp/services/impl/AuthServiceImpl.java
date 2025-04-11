package com.rideApp.RideApp.services.impl;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.LoginRequestDTO;
import com.rideApp.RideApp.DTO.LoginResponseDTO;
import com.rideApp.RideApp.DTO.OnBoardNewDriverDTO;
import com.rideApp.RideApp.DTO.SignupDTO;
import com.rideApp.RideApp.DTO.UserDTO;
import com.rideApp.RideApp.entities.Driver;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.enums.Role;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.exceptions.RuntimeConflictException;
import com.rideApp.RideApp.repository.UserRepository;
import com.rideApp.RideApp.security.JwtService;
import com.rideApp.RideApp.services.AuthService;
import com.rideApp.RideApp.services.DriverService;
import com.rideApp.RideApp.services.RiderService;
import com.rideApp.RideApp.services.SessionService;
import com.rideApp.RideApp.services.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final SessionService sessionService;

    @Override
    @Transactional
    public LoginResponseDTO signup(SignupDTO signupDTO) {
        User user = userRepository.findByEmail(signupDTO.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeConflictException(
                    "Cannot signup , user already exists with email " + signupDTO.getEmail());
        }
        User mappedUser = modelMapper.map(signupDTO, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser = userRepository.save(mappedUser);

        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        sessionService.generateNewSession(savedUser, refreshToken);

        return new LoginResponseDTO(accessToken, refreshToken, modelMapper.map(savedUser, UserDTO.class));
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        sessionService.generateNewSession(user, refreshToken);
        return new LoginResponseDTO(accessToken, refreshToken, modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional
    public DriverDTO onBoardNewDriver(Long userId, OnBoardNewDriverDTO onBoardNewDriverDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        if (user.getRoles().contains(Role.DRIVER))
            throw new RuntimeConflictException("User with id " + userId + " us already a driver");

        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(onBoardNewDriverDTO.getVehicleId())
                .isAvailable(true)
                .build();

        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);
        Driver savedDriver = driverService.createNewDriver(createDriver);
        DriverDTO savedDriverDto = modelMapper.map(savedDriver, DriverDTO.class);
        return savedDriverDto;
    }

    @Override
    @Transactional
    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        sessionService.validateSession(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        return new LoginResponseDTO(jwtService.generateAccessToken(user), refreshToken,
                modelMapper.map(user, UserDTO.class));
    }

    @Override
    public void logout(String refreshToken) {
        sessionService.invalidateSession(refreshToken);
    }

}
