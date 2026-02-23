package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Driver;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.DriverRepository;
import com.example.RideHailingApp.repository.RideRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

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
        Ride ride = rideRepository.findById(rideId).orElseThrow(() ->
                new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new RuntimeException("Ride not in REQUESTED state");
        }

        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);

        if (drivers.isEmpty()) {
            throw new RuntimeException("No drivers available");
        }

        drivers.sort(
                (d1,d2)-> {
                    double dist1 = calculateDistance(
                        ride.getPickupLat(),ride.getPickupLng(),
                        d1.getLatitude(),d1.getLongitude());
                    double dist2 = calculateDistance(
                            ride.getPickupLat(),ride.getPickupLng(),
                            d2.getLatitude(),d2.getLongitude());
                    return Double.compare(dist1, dist2);
                });
        Driver assignedDriver = null;
        for (Driver driver : drivers) {
            int updated = driverRepository.updateDriverStatusIfAvailable(driver.getId(),
                    DriverStatus.AVAILABLE, DriverStatus.BUSY);
            if (updated == 1) {
                assignedDriver = driver;
                break;
            }
        }
            if(assignedDriver == null){
                throw new RuntimeException("No drivers available");
            }
        ride.setDriverId(assignedDriver.getId());
        ride.setStatus(RideStatus.ASSIGNED);

        return rideRepository.save(ride);
    }

    private double calculateDistance(double lat1, double lon1,
                                     double lat2, double lon2) {

        final int R = 6371; // Earth radius in KM

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}