package com.example.RideHailingApp.entity;

import com.example.RideHailingApp.domain.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="drivers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    private Double latitude;
    private Double longitude;

    private String region;
    private String vehicleType;
    private LocalDateTime updatedAt;
}
