package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RideRepository riderRepository;
    private final DriverService driverService;
    private final GeoService geoService;


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

        return riderRepository.save(ride);
    }

    @Transactional
    public Ride assignDriver(Long rideId) {

        Ride ride = riderRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new RuntimeException("Ride not in REQUESTED state");
        }

        List<String> nearestDriverIds = geoService.findNearestDrivers(
                ride.getPickupLat(),
                ride.getPickupLng(),
                10
        );

        for (String driverIdStr : nearestDriverIds) {

            Long driverId = Long.parseLong(driverIdStr);

            if (driverService.tryAssignDriver(driverId)) {

                ride.setDriverId(driverId);
                ride.setStatus(RideStatus.ASSIGNED);

                return riderRepository.save(ride);
            }
        }

        throw new RuntimeException("No drivers available");
    }

}