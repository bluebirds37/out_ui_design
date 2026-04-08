package com.trailnote.admin.dashboard.infrastructure.persistence.jdbc;

import com.trailnote.admin.dashboard.domain.model.AdminDashboardOverview;
import com.trailnote.admin.dashboard.domain.repository.AdminDashboardQueryRepository;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAdminDashboardQueryRepository implements AdminDashboardQueryRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcAdminDashboardQueryRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public AdminDashboardOverview overview() {
    Integer totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trailnote.tn_user", Integer.class);
    Integer totalRoutes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trailnote.tn_route", Integer.class);
    Integer publishedRoutes = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM trailnote.tn_route WHERE status = 'PUBLISHED'",
        Integer.class
    );
    Integer totalComments = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trailnote.tn_route_comment", Integer.class);
    Integer totalFavorites = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trailnote.tn_route_favorite", Integer.class);
    return new AdminDashboardOverview(
        safe(totalUsers),
        safe(totalRoutes),
        safe(publishedRoutes),
        Math.max(0, safe(totalRoutes) - safe(publishedRoutes)),
        safe(totalComments),
        safe(totalFavorites),
        LocalDateTime.now()
    );
  }

  private int safe(Integer value) {
    return value == null ? 0 : value;
  }
}
