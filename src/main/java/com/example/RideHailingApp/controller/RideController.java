package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    // Create Ride
    @PostMapping
    public Ride createRide(@RequestBody CreateRideRequest request) {
        return rideService.createRide(request);
    }

    // Get Ride Status
    @GetMapping("/{id}")
    public Ride getRide(@PathVariable Long id) {
        return rideService.getRide(id);
    }

    // Assign Driver
    @PostMapping("/{id}/assign")
    public Ride assignDriver(@PathVariable Long id) {
        return rideService.assignDriver(id);
    }


}