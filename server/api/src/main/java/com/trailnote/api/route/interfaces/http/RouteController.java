package com.trailnote.api.route.interfaces.http;

import com.trailnote.api.route.domain.model.RouteDetail;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.application.RouteQueryService;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

  private final RouteQueryService routeQueryService;

  public RouteController(RouteQueryService routeQueryService) {
    this.routeQueryService = routeQueryService;
  }

  @GetMapping("/featured")
  public ApiResponse<List<RouteSummary>> featured() {
    return ApiResponse.success(routeQueryService.listFeaturedRoutes());
  }

  @GetMapping
  public ApiResponse<PageResponse<RouteSummary>> page(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long pageSize
  ) {
    return ApiResponse.success(routeQueryService.page(page, pageSize));
  }

  @GetMapping("/{routeId}")
  public ApiResponse<RouteDetail> detail(@PathVariable Long routeId) {
    return ApiResponse.success(routeQueryService.getRouteDetail(routeId));
  }
}
