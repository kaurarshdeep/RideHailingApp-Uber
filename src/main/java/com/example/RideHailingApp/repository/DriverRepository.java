package com.example.RideHailingApp.repository;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.entity.Driver;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface DriverRepository extends JpaRepository<Driver,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Driver> findByStatus(DriverStatus status);

    @Modifying
    @Query("""
        Update Driver d set d.status = :newStatus where d.id = :id and d.status = :currentStatus
""")
    int updateDriverStatusIfAvailable(@Param("id") Long id, @Param("currentStatus") DriverStatus currentStatus, @Param("newStatus") DriverStatus status);

}
