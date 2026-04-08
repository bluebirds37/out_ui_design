package com.trailnote.api.route.domain.model;

import java.util.List;

public record RouteSummary(
    Long id,
    String title,
    String coverUrl,
    String authorName,
    Long authorId,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer waypointCount,
    Integer favoriteCount,
    List<String> tags
) {
}
