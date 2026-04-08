package com.trailnote.admin.comment.domain.model;

import java.time.LocalDateTime;

public record AdminCommentRow(
    Long id,
    Long routeId,
    String routeTitle,
    Long userId,
    String authorName,
    String content,
    LocalDateTime createdAt
) {
}
