package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.RouteDifficulty;
import java.time.LocalDateTime;
import java.util.List;

public record CreatorRouteDraftSnapshot(
    Long id,
    String title,
    String coverUrl,
    String description,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer maxAltitudeM,
    String status,
    List<String> tags,
    List<RouteDraftWaypointInput> waypoints,
    LocalDateTime updatedAt
) {
}
