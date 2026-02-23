package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.dto.CreateRideRequest;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rides")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PostMapping
    public Ride createRide(@RequestBody CreateRideRequest request) {
        return riderService.createRide(request);
    }

    @PostMapping("/{rideId}/assign")
    public Ride assignDriver(@PathVariable Long rideId) {
        return riderService.assignDriver(rideId);
    }
}