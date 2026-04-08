package com.trailnote.api.interaction.domain.model;

import java.time.LocalDateTime;

public record RouteCommentItem(
    Long id,
    Long routeId,
    Long userId,
    String authorName,
    String authorAvatarUrl,
    String content,
    Boolean mine,
    LocalDateTime createdAt
) {
}
