package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.dto.UpdateLocationRequest;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.service.DriverService;
import com.example.RideHailingApp.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final RideService rideService;

    // Update driver location
    @PostMapping("/{id}/location")
    public void updateLocation(
            @PathVariable Long id,
            @RequestBody UpdateLocationRequest request) {

        driverService.updateLocation(
                id,
                request.getLatitude(),
                request.getLongitude()
        );
    }

    // Driver Accepts Ride
    @PostMapping("/{driverId}/accept")
    public Ride acceptRide(@PathVariable Long driverId) {
        return rideService.acceptRide(driverId);
    }
    //driver declines ride
    @PostMapping("/{driverId}/decline")
    public Ride declineRide(@PathVariable Long driverId) {
        return rideService.declineRide(driverId);
    }
}