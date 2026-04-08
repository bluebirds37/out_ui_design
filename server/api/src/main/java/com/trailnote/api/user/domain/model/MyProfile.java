package com.trailnote.api.user.domain.model;

public record MyProfile(
    Long id,
    String nickname,
    String avatarUrl,
    String bio,
    String city,
    String levelLabel,
    Integer publishedRouteCount,
    Integer favoriteRouteCount
) {
}
