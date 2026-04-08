package com.trailnote.api.route.domain.model;

public record RouteAggregate(
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
    RouteTagSet tags,
    RouteLifecycleStatus status
) {

  public static RouteAggregate of(
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
      RouteTagSet tags,
      RouteLifecycleStatus status
  ) {
    return new RouteAggregate(
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
        tags == null ? new RouteTagSet(java.util.List.of()) : tags,
        status
    );
  }

  public boolean isPublished() {
    return RouteLifecycleStatus.PUBLISHED.equals(status);
  }

  public PublishedRoute toPublishedRoute() {
    if (!isPublished()) {
      throw new IllegalStateException("route is not published: " + id);
    }
    return PublishedRoute.of(
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
        favoriteCount,
        commentCount,
        tags
    );
  }
}
