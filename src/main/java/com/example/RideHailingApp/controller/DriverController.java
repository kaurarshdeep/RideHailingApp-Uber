package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.dto.UpdateLocationRequest;
import com.example.RideHailingApp.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

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
}