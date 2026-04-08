package com.trailnote.admin.user.application;

import com.trailnote.admin.user.domain.model.AdminUserRow;
import com.trailnote.admin.user.domain.model.AdminUserSummary;
import com.trailnote.admin.user.domain.repository.AdminUserRepository;
import com.trailnote.common.api.PageResponse;
import org.springframework.stereotype.Service;

@Service
public class AdminUserApplicationService {

  private final AdminUserRepository repository;

  public AdminUserApplicationService(AdminUserRepository repository) {
    this.repository = repository;
  }

  public PageResponse<AdminUserRow> page(long page, long pageSize) {
    long safePage = Math.max(1, page);
    long safePageSize = Math.max(1, pageSize);
    long offset = Math.max(0, (safePage - 1) * safePageSize);
    int limit = (int) Math.min(safePageSize, Integer.MAX_VALUE);
    int offsetInt = (int) Math.min(offset, Integer.MAX_VALUE);
    long total = repository.countAll();
    return PageResponse.of(repository.list(limit, offsetInt), total, safePage, safePageSize);
  }

  public AdminUserRow detail(Long adminUserId) {
    return repository.findById(adminUserId);
  }

  public AdminUserSummary summary() {
    return repository.summary();
  }

  public AdminUserRow updateStatus(Long adminUserId, String status) {
    int updated = repository.updateStatus(adminUserId, status);
    if (updated == 0) {
      throw new IllegalArgumentException("admin user not found: " + adminUserId);
    }
    return repository.findById(adminUserId);
  }
}
