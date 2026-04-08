package com.trailnote.api.route.domain.model;

import java.util.List;

public record PublishedRoute(
    Long id,
    String title,
    String coverUrl,
    String description,
    Long authorId,
    RouteDifficulty difficulty,
    Double distanceKm,
    Integer durationMinutes,
    Integer ascentM,
    Integer maxAltitudeM,
    Integer favoriteCount,
    Integer commentCount,
    RouteTagSet tags
) {

  public static PublishedRoute of(
      Long id,
      String title,
      String coverUrl,
      String description,
      Long authorId,
      RouteDifficulty difficulty,
      Double distanceKm,
      Integer durationMinutes,
      Integer ascentM,
      Integer maxAltitudeM,
      Integer favoriteCount,
      Integer commentCount,
      RouteTagSet tags
  ) {
    return new PublishedRoute(
        id,
        title,
        coverUrl,
        description,
        authorId,
        difficulty,
        distanceKm,
        durationMinutes,
        ascentM,
        maxAltitudeM,
        favoriteCount == null ? 0 : favoriteCount,
        commentCount == null ? 0 : commentCount,
        tags == null ? new RouteTagSet(List.of()) : tags
    );
  }

  public RouteSummary toSummary(String authorName, int waypointCount) {
    return new RouteSummary(
        id,
        title,
        coverUrl,
        authorName,
        authorId,
        difficulty,
        distanceKm,
        durationMinutes,
        ascentM,
        waypointCount,
        favoriteCount,
        tags.values()
    );
  }

  public RouteDetail toDetail(String authorName, boolean favorited, List<WaypointSummary> waypoints) {
    return new RouteDetail(
        id,
        title,
        authorName,
        authorId,
        description,
        difficulty,
        distanceKm,
        durationMinutes,
        ascentM,
        maxAltitudeM,
        favoriteCount,
        commentCount,
        favorited,
        tags.values(),
        waypoints
    );
  }
}
