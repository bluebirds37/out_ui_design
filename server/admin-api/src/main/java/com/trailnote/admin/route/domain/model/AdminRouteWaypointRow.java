package com.trailnote.admin.route.domain.model;

public record AdminRouteWaypointRow(
    Long id,
    String title,
    String waypointType,
    String description,
    Integer sortOrder
) {
}
