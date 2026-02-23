package com.example.RideHailingApp.controller;

import com.example.RideHailingApp.dto.PaymentRequest;
import com.example.RideHailingApp.entity.Payment;
import com.example.RideHailingApp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment pay(@RequestBody @Valid PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}