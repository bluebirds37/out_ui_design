package com.trailnote.admin.user.interfaces.http;

import com.trailnote.admin.auth.application.AdminAuthorizationService;
import com.trailnote.admin.user.application.AdminUserApplicationService;
import com.trailnote.admin.user.domain.model.AdminUserRow;
import com.trailnote.admin.user.domain.model.AdminUserSummary;
import com.trailnote.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
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
@RequestMapping("/admin/users")
public class AdminUserController {

  private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "DISABLED");
  private final AdminAuthorizationService adminAuthorizationService;
  private final AdminUserApplicationService applicationService;

  public AdminUserController(
      AdminAuthorizationService adminAuthorizationService,
      AdminUserApplicationService applicationService
  ) {
    this.adminAuthorizationService = adminAuthorizationService;
    this.applicationService = applicationService;
  }

  @GetMapping
  public ApiResponse<com.trailnote.common.api.PageResponse<AdminUserRow>> page(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long pageSize
  ) {
    adminAuthorizationService.requirePermission("user:view");
    return ApiResponse.success(applicationService.page(page, pageSize));
  }

  @GetMapping("/{adminUserId}")
  public ApiResponse<AdminUserRow> detail(@PathVariable Long adminUserId) {
    adminAuthorizationService.requirePermission("user:view");
    return ApiResponse.success(applicationService.detail(adminUserId));
  }

  @GetMapping("/summary")
  public ApiResponse<AdminUserSummary> summary() {
    adminAuthorizationService.requirePermission("user:view");
    return ApiResponse.success(applicationService.summary());
  }

  @Transactional
  @PostMapping("/{adminUserId}/status")
  public ApiResponse<AdminUserRow> updateStatus(
      @PathVariable Long adminUserId,
      @Valid @RequestBody UpdateStatusRequest request
  ) {
    adminAuthorizationService.requirePermission("user:view");
    String normalized = request.status().toUpperCase();
    if (!ALLOWED_STATUSES.contains(normalized)) {
      throw new IllegalArgumentException("unsupported status: " + request.status());
    }
    return ApiResponse.success(applicationService.updateStatus(adminUserId, normalized));
  }

  public record UpdateStatusRequest(
      @NotBlank(message = "must not be blank")
      String status
  ) {
  }
}
