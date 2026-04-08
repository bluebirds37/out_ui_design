package com.trailnote.admin.user.interfaces.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.trailnote.admin.auth.application.AdminAuthorizationService;
import com.trailnote.admin.auth.support.AdminCurrentUserProvider;
import com.trailnote.admin.auth.application.AdminPermissionService;
import com.trailnote.admin.user.application.AdminUserApplicationService;
import com.trailnote.admin.user.domain.model.AdminUserRow;
import com.trailnote.admin.user.domain.model.AdminUserSummary;
import com.trailnote.admin.user.domain.repository.AdminUserRepository;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminUserControllerTest {

  private final StubGateway gateway = new StubGateway();
  private final AdminAuthorizationService authorizationService = new NoopAuthorizationService();
  private final AdminUserApplicationService applicationService = new AdminUserApplicationService(gateway);
  private final AdminUserController controller = new AdminUserController(authorizationService, applicationService);

  @BeforeEach
  void resetGateway() {
    gateway.reset();
  }

  @Test
  void pageReturnsPage() {
    ApiResponse<PageResponse<AdminUserRow>> response = controller.page(1, 5);

    assertEquals(1, response.data().total());
    assertEquals("ACTIVE", response.data().records().get(0).status());
    assertEquals(5, gateway.lastLimit);
    assertEquals(0, gateway.lastOffset);
  }

  @Test
  void detailDelegatesToGateway() {
    gateway.setDetailRow(createRow(2L, "DISABLED"));

    ApiResponse<AdminUserRow> response = controller.detail(2L);

    assertEquals("DISABLED", response.data().status());
    assertEquals(2, gateway.lastDetailId);
  }

  @Test
  void summaryReturnsCounts() {
    ApiResponse<AdminUserSummary> response = controller.summary();

    assertEquals(3, response.data().totalUsers());
    assertEquals(2, response.data().activeUsers());
  }

  @Test
  void updateStatusNormalizesStatus() {
    gateway.setDetailRow(createRow(1L, "DISABLED"));

    ApiResponse<AdminUserRow> response =
        controller.updateStatus(1L, new AdminUserController.UpdateStatusRequest("disabled"));

    assertEquals("DISABLED", response.data().status());
    assertEquals(1, gateway.lastUpdateId);
    assertEquals("DISABLED", gateway.lastUpdateStatus);
  }

  private static AdminUserRow createRow(Long id, String status) {
    return new AdminUserRow(
        id,
        "root_admin",
        "SUPER_ADMIN",
        status,
        LocalDateTime.of(2026, 3, 25, 10, 0, 0),
        LocalDateTime.of(2026, 3, 25, 12, 0, 0)
    );
  }

  private static final class StubGateway implements AdminUserRepository {

    private final List<AdminUserRow> listRows = List.of(createRow(1L, "ACTIVE"));
    private final AdminUserSummary summaryRow = new AdminUserSummary(3, 2, 1);
    private AdminUserRow detailRow = createRow(1L, "ACTIVE");
    private int updateResult = 1;
    int lastLimit;
    int lastOffset;
    long lastUpdateId;
    String lastUpdateStatus;
    long lastDetailId;

    void reset() {
      detailRow = createRow(1L, "ACTIVE");
      lastLimit = 0;
      lastOffset = 0;
      lastUpdateId = 0;
      lastUpdateStatus = null;
      lastDetailId = 0;
    }

    void setDetailRow(AdminUserRow detailRow) {
      this.detailRow = detailRow;
    }

    @Override
    public long countAll() {
      return 1L;
    }

    @Override
    public List<AdminUserRow> list(int limit, int offset) {
      this.lastLimit = limit;
      this.lastOffset = offset;
      return listRows;
    }

    @Override
    public AdminUserRow findById(Long id) {
      this.lastDetailId = id;
      return detailRow;
    }

    @Override
    public AdminUserRow findByUsername(String username) {
      return detailRow;
    }

    @Override
    public AdminUserRow findFirstActive() {
      return detailRow;
    }

    @Override
    public AdminUserSummary summary() {
      return summaryRow;
    }

    @Override
    public int updateStatus(Long id, String status) {
      this.lastUpdateId = id;
      this.lastUpdateStatus = status;
      return updateResult;
    }
  }

  private static final class NoopAuthorizationService extends AdminAuthorizationService {

    NoopAuthorizationService() {
      super(new AdminCurrentUserProvider(), new AdminPermissionService(null));
    }

    @Override
    public void requirePermission(String permissionCode) {
      // No-op in this controller unit test.
    }
  }
}
