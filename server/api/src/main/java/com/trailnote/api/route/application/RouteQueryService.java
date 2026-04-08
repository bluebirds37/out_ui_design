package com.trailnote.api.route.application;

import com.trailnote.api.route.domain.model.RouteDetail;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.domain.repository.RouteQueryRepository;
import com.trailnote.api.support.DemoCurrentUserProvider;
import com.trailnote.common.api.PageResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RouteQueryService {

  private final RouteQueryRepository repository;
  private final DemoCurrentUserProvider currentUserProvider;

  public RouteQueryService(
      RouteQueryRepository repository,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.currentUserProvider = currentUserProvider;
  }

  public List<RouteSummary> listFeaturedRoutes() {
    return repository.listFeaturedRoutes();
  }

  public PageResponse<RouteSummary> page(long page, long pageSize) {
    return repository.page(page, pageSize);
  }

  public RouteDetail getRouteDetail(Long routeId) {
    return repository.getRouteDetail(routeId, currentUserProvider.getCurrentUserId());
  }
}
