package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Driver;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.DriverRepository;
import com.example.RideHailingApp.repository.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private  final DriverRepository driverRepository;

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

    @Transactional
    public Ride assignDriver(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(()->
                new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new RuntimeException("Ride not in REQUESTED state");
        }

        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);

        if (drivers.isEmpty()) {
            throw new RuntimeException("No drivers available");
        }

        Driver driver = drivers.get(0); // temporary logic

        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        ride.setDriverId(driver.getId());
        ride.setStatus(RideStatus.ASSIGNED);

        return rideRepository.save(ride);
    }

}
