package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final RideService rideService;

    @PostMapping("/{id}/end")
    public Ride endTrip(@PathVariable Long id) {
        return rideService.endTrip(id);
    }
}