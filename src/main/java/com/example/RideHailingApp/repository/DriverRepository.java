package com.example.RideHailingApp.repository;

import com.example.RideHailingApp.domain.DriverStatus;
import com.example.RideHailingApp.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface DriverRepository extends JpaRepository<Driver,Long> {
    List<Driver> findByStatus(DriverStatus status);
}
