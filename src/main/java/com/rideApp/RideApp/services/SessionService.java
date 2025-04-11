package com.rideApp.RideApp.services;

import com.rideApp.RideApp.entities.User;

public interface SessionService {

    int SESSION_LIMIT = 1;

    void generateNewSession(User user, String refreshToken);

    void validateSession(String refreshToken);

    void invalidateSession(String refreshToken);

}
