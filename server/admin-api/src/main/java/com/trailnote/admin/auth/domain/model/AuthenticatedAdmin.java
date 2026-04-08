package com.trailnote.admin.auth.domain.model;

public record AuthenticatedAdmin(
    Long id,
    String username,
    String roleCode,
    String status
) {
}
