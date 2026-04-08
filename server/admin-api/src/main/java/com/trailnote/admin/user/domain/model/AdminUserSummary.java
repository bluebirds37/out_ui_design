package com.trailnote.admin.user.domain.model;

public record AdminUserSummary(
    long totalUsers,
    long activeUsers,
    long disabledUsers
) {
}
