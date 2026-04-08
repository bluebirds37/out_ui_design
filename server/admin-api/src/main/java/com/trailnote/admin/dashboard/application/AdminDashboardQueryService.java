package com.trailnote.admin.dashboard.application;

import com.trailnote.admin.dashboard.domain.model.AdminDashboardOverview;
import com.trailnote.admin.dashboard.domain.repository.AdminDashboardQueryRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardQueryService {

  private final AdminDashboardQueryRepository repository;

  public AdminDashboardQueryService(AdminDashboardQueryRepository repository) {
    this.repository = repository;
  }

  public AdminDashboardOverview overview() {
    return repository.overview();
  }
}
