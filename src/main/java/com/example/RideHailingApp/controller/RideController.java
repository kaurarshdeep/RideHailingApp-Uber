package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public Ride createRide(@Valid @RequestBody CreateRideRequest request) {
        return rideService.createRide(request);
    }

    @PostMapping("/{rideId}/assign")
    public Ride assignDriver(@PathVariable Long rideId) {
        return rideService.assignDriver(rideId);
    }
}