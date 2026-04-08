package com.trailnote.admin.dashboard.interfaces.http;

import com.trailnote.admin.auth.application.AdminAuthorizationService;
import com.trailnote.admin.dashboard.application.AdminDashboardQueryService;
import com.trailnote.admin.dashboard.domain.model.AdminDashboardOverview;
import com.trailnote.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

  private final AdminAuthorizationService adminAuthorizationService;
  private final AdminDashboardQueryService queryService;

  public AdminDashboardController(
      AdminAuthorizationService adminAuthorizationService,
      AdminDashboardQueryService queryService
  ) {
    this.adminAuthorizationService = adminAuthorizationService;
    this.queryService = queryService;
  }

  @GetMapping("/overview")
  public ApiResponse<AdminDashboardOverview> overview() {
    adminAuthorizationService.requirePermission("dashboard:view");
    return ApiResponse.success(queryService.overview());
  }
}
