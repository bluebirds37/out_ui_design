package com.trailnote.admin.dashboard.domain.model;

import java.time.LocalDateTime;

public record AdminDashboardOverview(
    Integer totalUsers,
    Integer totalRoutes,
    Integer publishedRoutes,
    Integer pendingOrDraftRoutes,
    Integer totalComments,
    Integer totalFavorites,
    LocalDateTime generatedAt
) {
}
