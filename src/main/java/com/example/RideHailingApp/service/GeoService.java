package com.example.RideHailingApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoService {

    private final StringRedisTemplate redisTemplate;

    private static final String DRIVER_GEO_KEY = "drivers:geo";

    public void updateDriverLocation(Long driverId, double lat, double lng) {
        redisTemplate.opsForGeo()
                .add(DRIVER_GEO_KEY,
                        new RedisGeoCommands.GeoLocation<>(
                                driverId.toString(),
                                new Point(lng, lat)
                        )
                );
    }

    public List<String> findNearestDrivers(double lat, double lng, int radiusKm) {

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVER_GEO_KEY,
                        new Circle(
                                new Point(lng, lat),
                                new Distance(radiusKm, Metrics.KILOMETERS)
                        )
                );

        if (results == null) return List.of();

        return results.getContent()
                .stream()
                .map(r -> r.getContent().getName())
                .toList();
    }
}