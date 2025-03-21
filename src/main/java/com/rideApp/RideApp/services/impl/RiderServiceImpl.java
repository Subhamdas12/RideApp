package com.rideApp.RideApp.services.impl;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.rideApp.RideApp.DTO.RideRequestDTO;
import com.rideApp.RideApp.entities.RideRequest;
import com.rideApp.RideApp.entities.Rider;
import com.rideApp.RideApp.entities.User;
import com.rideApp.RideApp.entities.enums.RideRequestStatus;
import com.rideApp.RideApp.entities.enums.Role;
import com.rideApp.RideApp.exceptions.ResourceNotFoundException;
import com.rideApp.RideApp.repository.RideRequestRepository;
import com.rideApp.RideApp.repository.RiderRepository;
import com.rideApp.RideApp.services.RiderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RiderRepository riderRepository;
    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequestDTO requestRide(RideRequestDTO rideRequestDTO) {
        Rider rider = getCurrentRider();
        RideRequest rideRequest = modelMapper.map(rideRequestDTO, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        return modelMapper.map(savedRideRequest, RideRequestDTO.class);
    }

    private Rider getCurrentRider() {
        User user = new User(1L, "Subham Das", "subham@gmail.com", "Alhama", Set.of(Role.RIDER));
        return riderRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Rider not associated with user id : " + user.getId()));
    }

}
