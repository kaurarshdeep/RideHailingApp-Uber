package com.example.RideHailingApp.repository;

import com.example.RideHailingApp.domain.RideStatus;
import com.example.RideHailingApp.entity.Ride;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface RideRepository extends JpaRepository<Ride,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);

    @Query(""" 
    SELECT r FROM Ride r WHERE r.driverId = :driverId AND r.status = :status """)
    Optional<Ride> findAssignedRide(@Param("driverId") Long driverId,@Param("status") RideStatus status);

    List<Ride> findByStatusAndAssignedAtBefore(RideStatus status,LocalDateTime time);


    @Modifying
    @Query("""
    UPDATE Ride r
    SET r.status = 'EXPIRED'
    WHERE r.id = :rideId
      AND r.status = 'ASSIGNED'
""")
    int expireRideIfStillAssigned(Long rideId);
}
