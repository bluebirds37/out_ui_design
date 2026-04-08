package com.trailnote.admin.route.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record AdminRouteDetail(
    Long id,
    String title,
    String authorName,
    String description,
    String status,
    String difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer maxAltitudeM,
    Integer favoriteCount,
    Integer commentCount,
    String tags,
    LocalDateTime publishedAt,
    LocalDateTime updatedAt,
    List<AdminRouteWaypointRow> waypoints
) {
}
