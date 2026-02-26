package com.example.RideHailingApp;

import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.entity.Ride;
import com.example.RideHailingApp.repository.DriverRepository;
import com.example.RideHailingApp.repository.RideRepository;
import com.example.RideHailingApp.service.DriverService;
import com.example.RideHailingApp.service.GeoService;
import com.example.RideHailingApp.service.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverService driverService;

    @Mock
    private GeoService geoService;

    @InjectMocks
    private RideService rideService;

    private Ride ride;

    @BeforeEach
    void setUp() {
        ride = Ride.builder()
                .id(1L)
                .status(RideStatus.REQUESTED)
                .pickupLat(28.6)
                .pickupLng(77.2)
                .destinationLat(28.7)
                .destinationLng(77.3)
                .build();
    }

    @Test
    void testAssignDriverSuccess() {

        Ride ride = Ride.builder()
                .id(1L)
                .status(RideStatus.REQUESTED)
                .pickupLat(28.6)
                .pickupLng(77.2)
                .destinationLat(28.7)
                .destinationLng(77.3)
                .build();



    when(rideRepository.findById(1L))
            .thenReturn(Optional.of(ride));

    when(geoService.findNearestDrivers(anyDouble(), anyDouble(), anyInt()))
            .thenReturn(List.of("10"));

    when(driverService.tryAssignDriver(10L))
            .thenReturn(true);

    when(rideRepository.save(any(Ride.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Ride result = rideService.assignDriver(1L);
        assertEquals(RideStatus.ASSIGNED, result.getStatus());
        assertEquals(10L, result.getDriverId());
    }


}