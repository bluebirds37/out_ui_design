package com.trailnote.admin.route.domain.model;

import java.time.LocalDateTime;

public record AdminRouteStatusResult(
    Long id,
    String status,
    LocalDateTime publishedAt,
    LocalDateTime updatedAt
) {
}
