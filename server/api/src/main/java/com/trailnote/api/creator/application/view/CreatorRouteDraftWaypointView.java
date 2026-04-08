package com.trailnote.api.creator.application.view;

import com.trailnote.api.route.domain.model.WaypointType;
import java.util.List;

public record CreatorRouteDraftWaypointView(
    Long id,
    String title,
    WaypointType waypointType,
    String description,
    Double latitude,
    Double longitude,
    Integer altitudeM,
    Integer sortOrder,
    List<CreatorRouteDraftMediaView> mediaList
) {
}
