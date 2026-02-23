package com.example.RideHailingApp.service;

import com.example.RideHailingApp.domain.PaymentStatus;
import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.dto.PaymentRequest;
import com.example.RideHailingApp.entity.Payment;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.PaymentRepository;
import com.example.RideHailingApp.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;

    @Transactional
    public Payment processPayment(PaymentRequest request) {

        Optional<Payment> existing =
                paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            return existing.get();
        }

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new RuntimeException("Ride not completed");
        }

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        return paymentRepository.save(payment);
    }
}