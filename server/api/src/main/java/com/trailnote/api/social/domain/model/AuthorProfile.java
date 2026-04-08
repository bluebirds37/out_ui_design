package com.trailnote.api.social.domain.model;

import com.trailnote.api.route.domain.model.RouteSummary;
import java.util.List;

public record AuthorProfile(
    Long id,
    String nickname,
    String avatarUrl,
    String bio,
    String city,
    String levelLabel,
    Integer followerCount,
    Integer followingCount,
    Integer publishedRouteCount,
    Boolean followed,
    List<RouteSummary> routes
) {
}
