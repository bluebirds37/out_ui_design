package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.RouteDifficulty;
import java.util.List;

public record SaveDraftCommand(
    Long routeId,
    String title,
    String coverUrl,
    String description,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer maxAltitudeM,
    List<String> tags,
    List<RouteDraftWaypointInput> waypoints
) {
}
