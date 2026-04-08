package com.trailnote.api.search.domain.model;

import com.trailnote.api.route.domain.model.RouteDifficulty;
import java.util.List;

public record MapRouteItem(
    Long routeId,
    String title,
    String coverUrl,
    String authorName,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer favoriteCount,
    Double latitude,
    Double longitude,
    List<String> tags
) {
}
