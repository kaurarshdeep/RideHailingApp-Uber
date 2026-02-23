package com.example.RideHailingApp.entity;

import com.example.RideHailingApp.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;
}