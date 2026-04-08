package com.trailnote.api.search.domain.model;

import com.trailnote.api.route.domain.model.RouteSummary;
import java.util.List;

public record SearchResult(
    String keyword,
    List<RouteSummary> routes,
    List<SearchAuthorItem> authors,
    List<SearchWaypointItem> waypoints
) {
}
