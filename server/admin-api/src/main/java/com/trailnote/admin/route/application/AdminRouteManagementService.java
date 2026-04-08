package com.trailnote.admin.route.application;

import com.trailnote.admin.route.domain.model.AdminRouteDetail;
import com.trailnote.admin.route.domain.model.AdminRouteRow;
import com.trailnote.admin.route.domain.model.AdminRouteStatusResult;
import com.trailnote.admin.route.domain.repository.AdminRouteManagementRepository;
import com.trailnote.common.api.PageResponse;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminRouteManagementService {

  private static final Set<String> ALLOWED_STATUS =
      Set.of("DRAFT", "PENDING_REVIEW", "PUBLISHED", "REJECTED", "ARCHIVED");

  private final AdminRouteManagementRepository repository;

  public AdminRouteManagementService(AdminRouteManagementRepository repository) {
    this.repository = repository;
  }

  public PageResponse<AdminRouteRow> page(long page, long pageSize) {
    return repository.page(page, pageSize);
  }

  public AdminRouteDetail detail(Long routeId) {
    return repository.detail(routeId);
  }

  @Transactional
  public AdminRouteStatusResult updateStatus(Long routeId, String status) {
    if (!ALLOWED_STATUS.contains(status)) {
      throw new IllegalArgumentException("unsupported route status: " + status);
    }
    return repository.updateStatus(routeId, status);
  }
}
