package com.trailnote.admin.user.domain.model;

import java.time.LocalDateTime;

public record AdminUserRow(
    Long id,
    String username,
    String roleCode,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
