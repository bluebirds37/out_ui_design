package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.WaypointType;
import java.util.List;

public record CreatorDraftWaypoint(
    Long id,
    String title,
    WaypointType waypointType,
    String description,
    Double latitude,
    Double longitude,
    Integer altitudeM,
    Integer sortOrder,
    List<CreatorDraftMedia> mediaList
) {

  public static CreatorDraftWaypoint from(RouteDraftWaypointInput input) {
    return new CreatorDraftWaypoint(
        input.id(),
        input.title(),
        input.waypointType(),
        input.description(),
        input.latitude(),
        input.longitude(),
        input.altitudeM(),
        input.sortOrder(),
        input.mediaList() == null ? List.of() : input.mediaList().stream().map(CreatorDraftMedia::from).toList()
    );
  }

  public void validate() {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("waypoint title must not be blank");
    }
    if (waypointType == null) {
      throw new IllegalArgumentException("waypointType must not be null");
    }
    if (latitude == null || latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("latitude out of range");
    }
    if (longitude == null || longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("longitude out of range");
    }
    if (sortOrder == null || sortOrder <= 0) {
      throw new IllegalArgumentException("sortOrder must be greater than 0");
    }
    mediaList.forEach(CreatorDraftMedia::validate);
  }
}
