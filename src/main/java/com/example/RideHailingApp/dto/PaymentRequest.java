package com.example.RideHailingApp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long rideId;

    @NotNull
    private Double amount;

    @NotBlank
    private String idempotencyKey;
}