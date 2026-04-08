package com.trailnote.api.search.domain.model;

public record SearchWaypointItem(
    Long id,
    Long routeId,
    String routeTitle,
    String title,
    String waypointType,
    String description,
    Double latitude,
    Double longitude
) {
}
