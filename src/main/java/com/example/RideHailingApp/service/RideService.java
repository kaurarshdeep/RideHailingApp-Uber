package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Driver;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.DriverRepository;
import com.example.RideHailingApp.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final DriverService driverService;
    private final GeoService geoService;

    // CREATE RIDE
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

    // GET RIDE
    public Ride getRide(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
    }

    // ASSIGN DRIVER (Redis + Atomic)
    @Transactional
    public Ride assignDriver(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
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
                System.out.println("Nearest drivers: " + driverId);
                ride.setStatus(RideStatus.ASSIGNED);

                return rideRepository.save(ride);

            }
        }

        throw new RuntimeException("No drivers available");
    }

    // DRIVER ACCEPT
    @Transactional
    public Ride acceptRide(Long driverId) {

        Ride ride = rideRepository
                .findByDriverIdAndStatus(driverId, RideStatus.ASSIGNED)
                .orElseThrow(() -> new RuntimeException("No ride to accept"));

        ride.setStatus(RideStatus.ACCEPTED);

        return rideRepository.save(ride);
    }

    @Transactional
    public Ride endTrip(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED &&
                ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException("Trip not active");
        }

        double fare = calculateFare(ride);

        ride.setFare(fare);
        ride.setStatus(RideStatus.COMPLETED);

        Driver driver = driverRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(DriverStatus.AVAILABLE);

        return rideRepository.save(ride);
    }

    private double calculateFare(Ride ride) {

        double baseFare = 50;

        double distance = calculateDistance(
                ride.getPickupLat(),
                ride.getPickupLng(),
                ride.getDestinationLat(),
                ride.getDestinationLng()
        );

        return baseFare + (distance * 10 * ride.getSurgeMultiplier());
    }

    private double calculateDistance(double lat1, double lon1,
                                     double lat2, double lon2) {

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}