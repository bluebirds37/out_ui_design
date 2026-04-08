package com.trailnote.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.trailnote.admin.auth.application.AdminAuthorizationService;
import com.trailnote.admin.auth.support.AdminCurrentUserProvider;
import com.trailnote.admin.auth.application.AdminPermissionService;
import com.trailnote.admin.auth.application.AdminSessionService;
import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import com.trailnote.admin.auth.interfaces.http.AdminAuthController;
import com.trailnote.admin.comment.application.AdminCommentQueryService;
import com.trailnote.admin.comment.domain.model.AdminCommentRow;
import com.trailnote.admin.comment.domain.repository.AdminCommentQueryRepository;
import com.trailnote.admin.dashboard.application.AdminDashboardQueryService;
import com.trailnote.admin.dashboard.domain.model.AdminDashboardOverview;
import com.trailnote.admin.dashboard.domain.repository.AdminDashboardQueryRepository;
import com.trailnote.admin.route.application.AdminRouteManagementService;
import com.trailnote.admin.route.domain.model.AdminRouteDetail;
import com.trailnote.admin.route.domain.model.AdminRouteRow;
import com.trailnote.admin.route.domain.model.AdminRouteStatusResult;
import com.trailnote.admin.route.domain.model.AdminRouteWaypointRow;
import com.trailnote.admin.route.domain.repository.AdminRouteManagementRepository;
import com.trailnote.admin.support.interfaces.http.AdminHealthController;
import com.trailnote.admin.comment.interfaces.http.AdminCommentController;
import com.trailnote.admin.dashboard.interfaces.http.AdminDashboardController;
import com.trailnote.admin.route.interfaces.http.AdminRouteController;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import com.trailnote.common.health.ServiceStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class AdminControllerCharacterizationTests {

  @Test
  void adminHealthControllerReturnsConfiguredServiceStatus() {
    AdminHealthController controller = new AdminHealthController();
    ReflectionTestUtils.setField(controller, "applicationName", "trailnote-admin");
    ReflectionTestUtils.setField(controller, "environment", "test");
    ReflectionTestUtils.setField(controller, "version", "2.0.0");

    ApiResponse<ServiceStatus> response = controller.health();

    assertEquals("trailnote-admin", response.data().service());
    assertEquals("test", response.data().environment());
    assertEquals("2.0.0", response.data().version());
    assertNotNull(response.data().timestamp());
  }

  @Test
  void adminAuthControllerDelegatesSessionAndPermissionReads() {
    StubAdminCurrentUserProvider currentUserProvider = new StubAdminCurrentUserProvider();
    StubAdminPermissionService permissionService = new StubAdminPermissionService();
    StubAdminSessionService sessionService = new StubAdminSessionService();
    AdminAuthController controller = new AdminAuthController(currentUserProvider, permissionService, sessionService);
    AuthenticatedAdmin admin = new AuthenticatedAdmin(1L, "root_admin", "SUPER_ADMIN", "ACTIVE");
    sessionService.loginSession = new AdminSessionService.AdminLoginSession(
        "token",
        LocalDateTime.of(2026, 4, 1, 12, 0),
        admin
    );
    currentUserProvider.currentUser = admin;
    permissionService.accessCodes = List.of("dashboard:view");
    permissionService.menus = List.of(new AdminPermissionService.AdminMenuNode(
        1L,
        null,
        "dashboard",
        "/dashboard",
        "LAYOUT",
        null,
        new AdminPermissionService.AdminMenuMeta("Dashboard", "lucide:home", 1, true),
        List.of()
    ));

    assertEquals("token", controller.login(new AdminAuthController.AdminLoginRequest("root_admin", "secret1")).data().accessToken());
    assertEquals("root_admin", controller.profile().data().username());
    assertEquals(List.of("dashboard:view"), controller.accessCodes().data());
    assertEquals("/dashboard", controller.menus().data().get(0).path());

    controller.logout("Bearer token");
    assertEquals("Bearer token", sessionService.lastAuthorizationHeader);
  }

  @Test
  void adminDashboardControllerAggregatesOverviewCounts() {
    AdminDashboardController controller = new AdminDashboardController(
        new NoopAuthorizationService(),
        new AdminDashboardQueryService(new StubDashboardQueryRepository())
    );

    ApiResponse<AdminDashboardOverview> response = controller.overview();

    assertEquals(5, response.data().totalUsers());
    assertEquals(9, response.data().totalRoutes());
    assertEquals(4, response.data().publishedRoutes());
    assertEquals(5, response.data().pendingOrDraftRoutes());
    assertEquals(11, response.data().totalComments());
    assertEquals(16, response.data().totalFavorites());
    assertNotNull(response.data().generatedAt());
  }

  @Test
  void adminCommentControllerReturnsPagedRows() {
    AdminCommentController controller = new AdminCommentController(
        new NoopAuthorizationService(),
        new AdminCommentQueryService(new StubCommentQueryRepository())
    );

    ApiResponse<PageResponse<AdminCommentRow>> response = controller.page(1, 10);

    assertEquals(1, response.data().total());
    assertEquals("湖畔步道", response.data().records().get(0).routeTitle());
  }

  @Test
  void adminRouteControllerReturnsPageDetailAndStatusUpdate() {
    AdminRouteController controller =
        new AdminRouteController(
            new NoopAuthorizationService(),
            new AdminRouteManagementService(new StubRouteManagementRepository())
        );

    assertEquals(1, controller.page(1, 10).data().total());
    assertEquals("山脊路线", controller.detail(8L).data().title());
    assertEquals(
        "PUBLISHED",
        controller.updateStatus(8L, new AdminRouteController.UpdateRouteStatusRequest("PUBLISHED")).data().status()
    );
  }

  private static final class StubAdminCurrentUserProvider extends AdminCurrentUserProvider {
    private AuthenticatedAdmin currentUser;

    @Override
    public AuthenticatedAdmin getCurrentUser() {
      return currentUser;
    }
  }

  private static final class StubAdminPermissionService extends AdminPermissionService {
    private List<String> accessCodes = List.of();
    private List<AdminMenuNode> menus = List.of();

    StubAdminPermissionService() {
      super(null);
    }

    @Override
    public List<String> getAccessCodes(String roleCode) {
      return accessCodes;
    }

    @Override
    public List<AdminMenuNode> getMenus(String roleCode) {
      return menus;
    }
  }

  private static final class StubAdminSessionService extends AdminSessionService {
    private AdminLoginSession loginSession;
    private String lastAuthorizationHeader;

    StubAdminSessionService() {
      super(null);
    }

    @Override
    public AdminLoginSession createSession(String requestedUsername) {
      return loginSession;
    }

    @Override
    public void logout(String authorizationHeader) {
      this.lastAuthorizationHeader = authorizationHeader;
    }
  }

  private static final class NoopAuthorizationService extends AdminAuthorizationService {
    NoopAuthorizationService() {
      super(new AdminCurrentUserProvider(), new AdminPermissionService(null));
    }

    @Override
    public void requirePermission(String permissionCode) {
      // Intentionally empty for controller characterization tests.
    }
  }

  private static final class StubDashboardQueryRepository implements AdminDashboardQueryRepository {
    @Override
    public AdminDashboardOverview overview() {
      return new AdminDashboardOverview(5, 9, 4, 5, 11, 16, LocalDateTime.now());
    }
  }

  private static final class StubCommentQueryRepository implements AdminCommentQueryRepository {
    @Override
    public PageResponse<AdminCommentRow> page(long page, long pageSize) {
      return PageResponse.of(List.of(new AdminCommentRow(
          3L,
          7L,
          "湖畔步道",
          9L,
          "Alice",
          "nice",
          LocalDateTime.of(2026, 4, 1, 10, 0)
      )), 1, page, pageSize);
    }
  }

  private static final class StubRouteManagementRepository implements AdminRouteManagementRepository {
    @Override
    public PageResponse<AdminRouteRow> page(long page, long pageSize) {
      return PageResponse.of(List.of(new AdminRouteRow(
          8L,
          "山脊路线",
          "Alice",
          "PENDING_REVIEW",
          5,
          12,
          LocalDateTime.of(2026, 4, 1, 8, 0)
      )), 1, page, pageSize);
    }

    @Override
    public AdminRouteDetail detail(Long routeId) {
      return new AdminRouteDetail(
          8L,
          "山脊路线",
          "Alice",
          "desc",
          "PENDING_REVIEW",
          "ADVANCED",
          13.6,
          200,
          600,
          1280,
          12,
          4,
          "高山,挑战",
          null,
          LocalDateTime.of(2026, 4, 1, 8, 0),
          List.of(new AdminRouteWaypointRow(1L, "起点", "TRAILHEAD", "desc", 1))
      );
    }

    @Override
    public AdminRouteStatusResult updateStatus(Long routeId, String status) {
      return new AdminRouteStatusResult(
          8L,
          "PUBLISHED",
          LocalDateTime.of(2026, 4, 1, 12, 0),
          LocalDateTime.of(2026, 4, 1, 12, 0)
      );
    }
  }
}
