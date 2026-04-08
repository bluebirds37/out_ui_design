package com.trailnote.api.route.domain.model;

import java.util.List;

public record RouteDetail(
    Long id,
    String title,
    String authorName,
    Long authorId,
    String description,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer maxAltitudeM,
    Integer favoriteCount,
    Integer commentCount,
    Boolean favorited,
    List<String> tags,
    List<WaypointSummary> waypoints
) {
}
