package com.trailnote.api.creator.application.view;

import com.trailnote.api.creator.domain.model.CreatorRouteDraftSnapshot;
import org.springframework.stereotype.Component;

@Component
public class CreatorRouteDraftViewMapper {

  public CreatorRouteDraftView toView(CreatorRouteDraftSnapshot snapshot) {
    if (snapshot == null) {
      return null;
    }
    return new CreatorRouteDraftView(
        snapshot.id(),
        snapshot.title(),
        snapshot.coverUrl(),
        snapshot.description(),
        snapshot.difficulty(),
        snapshot.distanceKm(),
        snapshot.durationMinutes(),
        snapshot.ascentM(),
        snapshot.maxAltitudeM(),
        snapshot.status(),
        snapshot.tags(),
        snapshot.waypoints().stream()
            .map(waypoint -> new CreatorRouteDraftWaypointView(
                waypoint.id(),
                waypoint.title(),
                waypoint.waypointType(),
                waypoint.description(),
                waypoint.latitude(),
                waypoint.longitude(),
                waypoint.altitudeM(),
                waypoint.sortOrder(),
                waypoint.mediaList() == null ? java.util.List.of() : waypoint.mediaList().stream()
                    .map(media -> new CreatorRouteDraftMediaView(
                        media.id(),
                        media.mediaType(),
                        media.coverUrl(),
                        media.mediaUrl(),
                        media.durationSeconds()
                    ))
                    .toList()
            ))
            .toList(),
        snapshot.updatedAt()
    );
  }
}
