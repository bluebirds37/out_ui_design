package com.trailnote.api.creator.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record CreatorRouteRow(
    Long id,
    String title,
    String coverUrl,
    String status,
    Double distanceKm,
    Integer durationMinutes,
    Integer waypointCount,
    Integer favoriteCount,
    Integer commentCount,
    List<String> tags,
    LocalDateTime updatedAt
) {
}
