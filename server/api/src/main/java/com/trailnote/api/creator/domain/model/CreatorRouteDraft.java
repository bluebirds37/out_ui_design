package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.RouteDifficulty;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteTagSet;
import java.util.HashSet;
import java.util.List;

public record CreatorRouteDraft(
    Long routeId,
    String title,
    String coverUrl,
    String description,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer maxAltitudeM,
    RouteTagSet tags,
    List<CreatorDraftWaypoint> waypoints,
    String currentStatus,
    CreatorRouteDraftSnapshot persistedDetail
) {

  public static CreatorRouteDraft from(SaveDraftCommand command) {
    return new CreatorRouteDraft(
        command.routeId(),
        command.title(),
        command.coverUrl(),
        command.description(),
        command.difficulty(),
        command.distanceKm(),
        command.durationMinutes(),
        command.ascentM(),
        command.maxAltitudeM(),
        RouteTagSet.fromList(command.tags()),
        command.waypoints() == null ? List.of() : command.waypoints().stream().map(CreatorDraftWaypoint::from).toList(),
        null,
        null
    );
  }

  public static CreatorRouteDraft from(CreatorRouteDraftSnapshot detail) {
    return new CreatorRouteDraft(
        detail.id(),
        detail.title(),
        detail.coverUrl(),
        detail.description(),
        detail.difficulty(),
        detail.distanceKm(),
        detail.durationMinutes(),
        detail.ascentM(),
        detail.maxAltitudeM(),
        RouteTagSet.fromList(detail.tags()),
        detail.waypoints() == null ? List.of() : detail.waypoints().stream().map(CreatorDraftWaypoint::from).toList(),
        detail.status(),
        detail
    );
  }

  public void validateForSave() {
    if (distanceKm == null || distanceKm <= 0) {
      throw new IllegalArgumentException("distanceKm must be greater than 0");
    }
    if (durationMinutes == null || durationMinutes <= 0) {
      throw new IllegalArgumentException("durationMinutes must be greater than 0");
    }
    if (ascentM == null || ascentM < 0) {
      throw new IllegalArgumentException("ascentM must not be negative");
    }
    if (maxAltitudeM == null || maxAltitudeM < 0) {
      throw new IllegalArgumentException("maxAltitudeM must not be negative");
    }
    if (waypoints == null || waypoints.isEmpty()) {
      throw new IllegalArgumentException("at least one waypoint is required");
    }

    HashSet<Integer> sortOrders = new HashSet<>();
    waypoints.forEach(waypoint -> {
      waypoint.validate();
      if (!sortOrders.add(waypoint.sortOrder())) {
        throw new IllegalArgumentException("duplicate waypoint sortOrder: " + waypoint.sortOrder());
      }
    });
  }

  public String resolveNextPublishStatus() {
    RouteLifecycleStatus status = RouteLifecycleStatus.fromValue(currentStatus);
    if (RouteLifecycleStatus.PUBLISHED.equals(status)) {
      return status.value();
    }
    return RouteLifecycleStatus.PENDING_REVIEW.value();
  }
}
