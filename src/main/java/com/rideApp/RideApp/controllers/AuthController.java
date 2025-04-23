package com.rideApp.RideApp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideApp.RideApp.DTO.DriverDTO;
import com.rideApp.RideApp.DTO.LoginRequestDTO;
import com.rideApp.RideApp.DTO.LoginResponseDTO;
import com.rideApp.RideApp.DTO.OnBoardNewDriverDTO;
import com.rideApp.RideApp.DTO.SignupDTO;
import com.rideApp.RideApp.DTO.UserDTO;
import com.rideApp.RideApp.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody @Valid SignupDTO signupDTO,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        LoginResponseDTO loginResponseDTO = authService.signup(signupDTO);
        Cookie accessTokenCookie = new Cookie("access_token", loginResponseDTO.getAccessToken());
        Cookie refreshTokencookie = new Cookie("refresh_token", loginResponseDTO.getRefreshToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        refreshTokencookie.setHttpOnly(true);
        refreshTokencookie.setPath("/");
        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokencookie);
        return new ResponseEntity<>(loginResponseDTO.getUserDTO(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        Cookie accessTokenCookie = new Cookie("access_token", loginResponseDTO.getAccessToken());
        Cookie refreshTokencookie = new Cookie("refresh_token", loginResponseDTO.getRefreshToken());
        accessTokenCookie.setMaxAge(10 * 60);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        refreshTokencookie.setMaxAge(6 * 30 * 24 * 60 * 60);
        refreshTokencookie.setHttpOnly(true);
        refreshTokencookie.setPath("/");
        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokencookie);
        return ResponseEntity.ok(loginResponseDTO.getUserDTO());
    }

    @PostMapping("/onBoardNewDriver/{userId}")
    ResponseEntity<DriverDTO> onBoardNewDriver(@PathVariable Long userId,
            @RequestBody OnBoardNewDriverDTO onBoardNewDriverDTO) {
        return new ResponseEntity<>(authService.onBoardNewDriver(userId,
                onBoardNewDriverDTO), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserDTO> refreshAccessToken(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            throw new AuthenticationServiceException("No cookies present in the request");
        }
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refresh_token".equals(cookie.getName())).findFirst().map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        LoginResponseDTO loginResponseDTO = authService.refreshAccessToken(refreshToken);
        Cookie accessTokenCookie = new Cookie("access_token", loginResponseDTO.getAccessToken());
        accessTokenCookie.setMaxAge(10 * 60);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        httpServletResponse.addCookie(accessTokenCookie);

        return ResponseEntity.ok(loginResponseDTO.getUserDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            throw new AuthenticationServiceException("No cookies present in the request");
        }
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refresh_token".equals(cookie.getName())).findFirst().map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        authService.logout(refreshToken);

        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();

    }

}
