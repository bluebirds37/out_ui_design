package com.trailnote.api.creator.domain.model;

import java.time.LocalDateTime;

public record PublishRouteResult(
    Long routeId,
    String status,
    LocalDateTime updatedAt
) {
}
