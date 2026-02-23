package com.example.RideHailingApp.entity;

import com.example.RideHailingApp.domain.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="rides")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long riderId;
    @Enumerated(EnumType.STRING)
    private RideStatus status;
    private Long driverId;
    private LocalDateTime createdAt;
    private Double surgeMultiplier;
    private Double pickupLat;
    private Double pickupLng;
    private Double destinationLat;
    private Double destinationLng;
    private Double fare;

}
