package com.trailnote.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.trailnote.api.route.domain.model.RouteAggregate;
import com.trailnote.api.route.domain.model.PublishedRoute;
import com.trailnote.api.route.domain.model.RouteDifficulty;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.domain.model.WaypointSummary;
import java.util.List;
import org.junit.jupiter.api.Test;

class RouteDomainBehaviorTests {

  @Test
  void publishedRouteBuildsSummaryFromSharedState() {
    PublishedRoute route = PublishedRoute.of(
        5L,
        "山脊路线",
        "cover.png",
        "desc",
        2L,
        RouteDifficulty.ADVANCED,
        12.5,
        180,
        400,
        1200,
        8,
        3,
        RouteTagSet.fromList(List.of("高山", "挑战"))
    );

    assertEquals("高山", route.toSummary("Alice", 4).tags().get(0));
    assertEquals(4, route.toSummary("Alice", 4).waypointCount());
  }

  @Test
  void publishedRouteBuildsDetailWithFavoritedFlag() {
    PublishedRoute route = PublishedRoute.of(
        5L,
        "山脊路线",
        "cover.png",
        "desc",
        2L,
        RouteDifficulty.ADVANCED,
        12.5,
        180,
        400,
        1200,
        8,
        3,
        RouteTagSet.fromList(List.of("高山", "挑战"))
    );

    assertTrue(route.toDetail("Alice", true, List.<WaypointSummary>of()).favorited());
    assertEquals("挑战", route.toDetail("Alice", true, List.<WaypointSummary>of()).tags().get(1));
  }

  @Test
  void routeAggregateRejectsNonPublishedRouteWhenProjectingPublishedView() {
    RouteAggregate route = RouteAggregate.of(
        6L,
        "草稿路线",
        "cover.png",
        "desc",
        2L,
        RouteDifficulty.BEGINNER,
        3.2,
        40,
        60,
        200,
        0,
        0,
        RouteTagSet.fromList(List.of("草稿")),
        RouteLifecycleStatus.DRAFT
    );

    IllegalStateException error = assertThrows(IllegalStateException.class, route::toPublishedRoute);
    assertEquals("route is not published: 6", error.getMessage());
  }
}
