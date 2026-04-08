package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.WaypointType;
import java.util.List;

public record RouteDraftWaypointInput(
    Long id,
    String title,
    WaypointType waypointType,
    String description,
    Double latitude,
    Double longitude,
    Integer altitudeM,
    Integer sortOrder,
    List<RouteDraftMediaInput> mediaList
) {
}
