package com.example.RideHailingApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.annotation.EnableAsync;

@Entity
@Table(name="payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tripId;

    private String status;

    private String pspReference;

    private String idempotencyKey;
}
