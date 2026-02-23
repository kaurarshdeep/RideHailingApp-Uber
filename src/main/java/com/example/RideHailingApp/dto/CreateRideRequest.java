package com.example.RideHailingApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRideRequest {

    @NotNull
    private Long riderId;

    @NotNull
    private Double pickupLat;

    @NotNull
    private Double pickupLng;

    @NotNull
    private Double destinationLat;

    @NotNull
    private Double destinationLng;
}
