package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.entity.Driver;
import com.example.RideHailingApp.exception.DriverNotFoundException;
import com.example.RideHailingApp.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final GeoService geoService;

    @Transactional
    public void updateLocation(Long driverId, double lat, double lng) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        driver.setLatitude(lat);
        driver.setLongitude(lng);

        geoService.updateDriverLocation(driverId, lat, lng);
    }

    // Atomic status update
    @Transactional
    public boolean tryAssignDriver(Long driverId) {

        int updated = driverRepository.updateDriverStatusIfAvailable(
                driverId,
                DriverStatus.AVAILABLE,
                DriverStatus.BUSY
        );

        return updated == 1;
    }

    @Transactional
    public void makeDriverAvailable(Long driverId) {

        driverRepository.updateDriverStatusIfAvailable(
                driverId,
                DriverStatus.BUSY,
                DriverStatus.AVAILABLE
        );
    }
}