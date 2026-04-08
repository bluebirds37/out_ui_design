package com.trailnote.admin.auth.interfaces.http;

import com.trailnote.admin.auth.support.AdminCurrentUserProvider;
import com.trailnote.admin.auth.application.AdminPermissionService;
import com.trailnote.admin.auth.application.AdminSessionService;
import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import com.trailnote.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

  private final AdminCurrentUserProvider currentUserProvider;
  private final AdminPermissionService adminPermissionService;
  private final AdminSessionService adminSessionService;

  public AdminAuthController(
      AdminCurrentUserProvider currentUserProvider,
      AdminPermissionService adminPermissionService,
      AdminSessionService adminSessionService
  ) {
    this.currentUserProvider = currentUserProvider;
    this.adminPermissionService = adminPermissionService;
    this.adminSessionService = adminSessionService;
  }

  @PostMapping("/login")
  public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
    AdminSessionService.AdminLoginSession session = adminSessionService.createSession(request.username());
    return ApiResponse.success(
        new AdminLoginResponse(
            session.accessToken(),
            session.admin().username(),
            session.admin().roleCode(),
            session.expiresAt()
        )
    );
  }

  @GetMapping("/profile")
  public ApiResponse<AdminProfileResponse> profile() {
    AuthenticatedAdmin adminUser = currentUserProvider.getCurrentUser();
    return ApiResponse.success(
        new AdminProfileResponse(
            String.valueOf(adminUser.id()),
            adminUser.username(),
            buildRealName(adminUser),
            "",
            "TrailNote 后台管理员",
            "/analytics",
            List.of(adminUser.roleCode())
        )
    );
  }

  @GetMapping("/access-codes")
  public ApiResponse<List<String>> accessCodes() {
    AuthenticatedAdmin adminUser = currentUserProvider.getCurrentUser();
    return ApiResponse.success(adminPermissionService.getAccessCodes(adminUser.roleCode()));
  }

  @GetMapping("/menus")
  public ApiResponse<List<AdminMenuRoute>> menus() {
    AuthenticatedAdmin adminUser = currentUserProvider.getCurrentUser();
    return ApiResponse.success(
        adminPermissionService.getMenus(adminUser.roleCode()).stream()
            .map(this::toMenuRoute)
            .toList()
    );
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
    adminSessionService.logout(authorizationHeader);
    return ApiResponse.successMessage("logout success");
  }

  public record AdminLoginRequest(
      @NotBlank(message = "must not be blank")
      String username,
      @NotBlank(message = "must not be blank")
      @Size(min = 6, message = "must be at least 6 characters")
      String password
  ) {
  }

  public record AdminLoginResponse(String accessToken, String username, String role, LocalDateTime expiresAt) {
  }

  public record AdminProfileResponse(
      String userId,
      String username,
      String realName,
      String avatar,
      String desc,
      String homePath,
      List<String> roles
  ) {
  }

  public record AdminRouteMeta(
      String title,
      String icon,
      Integer order,
      Boolean affixTab
  ) {
  }

  public record AdminMenuRoute(
      String name,
      String path,
      String component,
      String redirect,
      AdminRouteMeta meta,
      List<AdminMenuRoute> children
  ) {
  }

  private static String buildRealName(AuthenticatedAdmin adminUser) {
    return switch (adminUser.roleCode()) {
      case "SUPER_ADMIN" -> "TrailNote Super Admin";
      case "OPERATOR" -> "TrailNote Operator";
      default -> "TrailNote Admin";
    };
  }

  private AdminMenuRoute toMenuRoute(AdminPermissionService.AdminMenuNode node) {
    return new AdminMenuRoute(
        node.name(),
        node.path(),
        node.component(),
        node.redirect(),
        new AdminRouteMeta(
            node.meta().title(),
            node.meta().icon(),
            node.meta().order(),
            node.meta().affixTab()
        ),
        node.children().stream().map(this::toMenuRoute).toList()
    );
  }
}
