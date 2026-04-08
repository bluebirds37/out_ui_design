package com.trailnote.api.route.domain.repository;

import com.trailnote.api.route.domain.model.RouteDetail;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.common.api.PageResponse;
import java.util.List;

public interface RouteQueryRepository {
  List<RouteSummary> listFeaturedRoutes();
  PageResponse<RouteSummary> page(long page, long pageSize);
  RouteDetail getRouteDetail(Long routeId, Long currentUserId);
}
