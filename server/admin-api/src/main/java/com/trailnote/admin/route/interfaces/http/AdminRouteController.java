package com.trailnote.admin.route.interfaces.http;

import com.trailnote.admin.auth.application.AdminAuthorizationService;
import com.trailnote.admin.route.application.AdminRouteManagementService;
import com.trailnote.admin.route.domain.model.AdminRouteDetail;
import com.trailnote.admin.route.domain.model.AdminRouteRow;
import com.trailnote.admin.route.domain.model.AdminRouteStatusResult;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin/routes")
public class AdminRouteController {

  private final AdminAuthorizationService adminAuthorizationService;
  private final AdminRouteManagementService managementService;

  public AdminRouteController(
      AdminAuthorizationService adminAuthorizationService,
      AdminRouteManagementService managementService
  ) {
    this.adminAuthorizationService = adminAuthorizationService;
    this.managementService = managementService;
  }

  @GetMapping
  public ApiResponse<PageResponse<AdminRouteRow>> page(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long pageSize
  ) {
    adminAuthorizationService.requirePermission("route:view");
    return ApiResponse.success(managementService.page(page, pageSize));
  }

  @GetMapping("/{routeId}")
  public ApiResponse<AdminRouteDetail> detail(@PathVariable Long routeId) {
    adminAuthorizationService.requirePermission("route:view");
    return ApiResponse.success(managementService.detail(routeId));
  }

  @Transactional
  @PostMapping("/{routeId}/status")
  public ApiResponse<AdminRouteStatusResult> updateStatus(
      @PathVariable Long routeId,
      @Valid @RequestBody UpdateRouteStatusRequest request
  ) {
    adminAuthorizationService.requirePermission("route:view");
    return ApiResponse.success(managementService.updateStatus(routeId, request.status()));
  }

  public record UpdateRouteStatusRequest(
      @NotBlank(message = "must not be blank")
      String status
  ) {
  }
}
