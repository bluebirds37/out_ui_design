package com.trailnote.api.route.domain.model;

import java.util.List;

public record WaypointSummary(
    Long id,
    String title,
    WaypointType type,
    String description,
    Double latitude,
    Double longitude,
    Integer altitudeM,
    Integer sortOrder,
    List<WaypointMedia> mediaList
) {
}
