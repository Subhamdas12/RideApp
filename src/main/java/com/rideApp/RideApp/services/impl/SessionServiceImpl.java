package com.rideApp.RideApp.services.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import com.rideApp.RideApp.entities.Session;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.repository.SessionRepository;
import com.rideApp.RideApp.services.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public void generateNewSession(User user, String refreshToken) {
        List<Session> sessionList = sessionRepository.findByUser(user);
        if (sessionList.size() == SESSION_LIMIT) {
            sessionList.sort(Comparator.comparing(Session::getLastUsedAt));
            Session leastRecentlyUsedSession = sessionList.getFirst();
            sessionRepository.delete(leastRecentlyUsedSession);
        }

        Session newSession = Session.builder().user(user).refreshToken(refreshToken).build();
        sessionRepository.save(newSession);

    }

    @Override
    public void validateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("No Session found for refresh token "));
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Override
    public void invalidateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("No Session found for refresh token"));

        sessionRepository.delete(session);
    }

}
