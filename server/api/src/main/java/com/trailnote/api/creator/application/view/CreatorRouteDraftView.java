package com.trailnote.api.creator.application.view;

import com.trailnote.api.route.domain.model.RouteDifficulty;
import java.time.LocalDateTime;
import java.util.List;

public record CreatorRouteDraftView(
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
    List<CreatorRouteDraftWaypointView> waypoints,
    LocalDateTime updatedAt
) {
}
