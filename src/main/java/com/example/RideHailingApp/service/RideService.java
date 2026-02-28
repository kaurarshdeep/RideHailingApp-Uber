package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Driver;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.exception.DriverNotFoundException;
import com.example.RideHailingApp.exception.InvalidRideStateException;
import com.example.RideHailingApp.exception.RideNotFoundException;
import com.example.RideHailingApp.repository.DriverRepository;
import com.example.RideHailingApp.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Ride not in REQUESTED state");
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
                ride.setAssignedAt(LocalDateTime.now());

                return rideRepository.save(ride);

            }
        }

        throw new DriverNotFoundException("No drivers available");
    }

    // DRIVER ACCEPT
    @Transactional
    public Ride acceptRide(Long driverId) {

        Ride ride = rideRepository
                .findByDriverIdAndStatus(driverId, RideStatus.ASSIGNED)
                .orElseThrow(() -> new RideNotFoundException("No ride to accept"));

        ride.setStatus(RideStatus.ACCEPTED);

        return rideRepository.save(ride);
    }

    @Transactional
    public Ride endTrip(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED &&
                ride.getStatus() != RideStatus.STARTED) {
            throw new InvalidRideStateException("Trip not active");
        }

        double fare = calculateFare(ride);

        ride.setFare(fare);
        ride.setStatus(RideStatus.COMPLETED);

        Driver driver = driverRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        driver.setStatus(DriverStatus.AVAILABLE);

        return ride;
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

    @Scheduled(fixedRate = 5000) // runs every 5 sec
    @Transactional
    public void expireUnacceptedRides() {

        LocalDateTime expiryTime = LocalDateTime.now().minusSeconds(30);

        List<Ride> expiredRides =
                rideRepository.findByStatusAndAssignedAtBefore(
                        RideStatus.ASSIGNED,
                        expiryTime
                );

        for (Ride ride : expiredRides) {
            int updated = rideRepository.expireRideIfStillAssigned(ride.getId());

            if (updated == 1) {
                driverService.makeDriverAvailable(ride.getDriverId());
            }
            if (ride.getDriverId() != null) {
                driverService.makeDriverAvailable(ride.getDriverId());
            }
            ride.setDriverId(null);
        }
    }

    @Transactional
    public Ride declineRide(Long driverId) {

        Ride ride = rideRepository
                .findByDriverIdAndStatus(driverId, RideStatus.ASSIGNED)
                .orElseThrow(() -> new RideNotFoundException("No ride to decline"));

        // Make driver available again
        driverRepository.updateDriverStatusIfAvailable(
                driverId,
                DriverStatus.BUSY,
                DriverStatus.AVAILABLE
        );

        // Remove driver from ride
        ride.setDriverId(null);
        ride.setStatus(RideStatus.REQUESTED);

        rideRepository.save(ride);

        // Try assigning next nearest driver
        return assignDriver(ride.getId());
    }
}