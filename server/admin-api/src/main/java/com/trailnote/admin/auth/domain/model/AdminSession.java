package com.trailnote.admin.auth.domain.model;

import java.time.LocalDateTime;

public record AdminSession(
    Long sessionId,
    String accessToken,
    LocalDateTime expiresAt,
    AuthenticatedAdmin admin
) {
}
