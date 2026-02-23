package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;

    public Ride createRide(CreateRideRequest request) {

        Ride ride = Ride.builder()
                .riderId(request.getRiderId())
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .destinationLat(request.getDestinationLat())
                .destinationLng(request.getDestinationLng())
                .status(RideStatus.REQUESTED)
                .surgeMultiplier(1.0)
                .createdAt(LocalDateTime.now())
                .build();

        return rideRepository.save(ride);
    }
}
