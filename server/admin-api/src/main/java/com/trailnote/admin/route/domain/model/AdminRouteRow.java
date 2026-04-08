package com.trailnote.admin.route.domain.model;

import java.time.LocalDateTime;

public record AdminRouteRow(
    Long id,
    String title,
    String authorName,
    String status,
    Integer waypointCount,
    Integer favoriteCount,
    LocalDateTime updatedAt
) {
}
