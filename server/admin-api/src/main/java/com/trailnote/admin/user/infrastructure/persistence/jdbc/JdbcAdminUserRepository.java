package com.trailnote.admin.user.infrastructure.persistence.jdbc;

import com.trailnote.admin.user.domain.model.AdminUserRow;
import com.trailnote.admin.user.domain.model.AdminUserSummary;
import com.trailnote.admin.user.domain.repository.AdminUserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAdminUserRepository implements AdminUserRepository {

  private static final String LIST_SQL = """
      SELECT
        id,
        username,
        role_code,
        status,
        created_at,
        updated_at
      FROM trailnote_admin.tn_admin_user
      ORDER BY updated_at DESC, id DESC
      LIMIT ? OFFSET ?
      """;
  private static final String DETAIL_SQL = """
      SELECT
        id,
        username,
        role_code,
        status,
        created_at,
        updated_at
      FROM trailnote_admin.tn_admin_user
      WHERE id = ?
      """;
  private static final String DETAIL_BY_USERNAME_SQL = """
      SELECT
        id,
        username,
        role_code,
        status,
        created_at,
        updated_at
      FROM trailnote_admin.tn_admin_user
      WHERE username = ?
      """;
  private static final String FIRST_ACTIVE_SQL = """
      SELECT
        id,
        username,
        role_code,
        status,
        created_at,
        updated_at
      FROM trailnote_admin.tn_admin_user
      WHERE status = 'ACTIVE'
      ORDER BY
        CASE role_code
          WHEN 'SUPER_ADMIN' THEN 0
          ELSE 1
        END,
        id ASC
      LIMIT 1
      """;
  private static final String SUMMARY_SQL = """
      SELECT
        COUNT(*) AS total_users,
        SUM(status = 'ACTIVE') AS active_users,
        SUM(status = 'DISABLED') AS disabled_users
      FROM trailnote_admin.tn_admin_user
      """;
  private static final String COUNT_SQL = "SELECT COUNT(*) FROM trailnote_admin.tn_admin_user";
  private static final String UPDATE_STATUS_SQL = """
      UPDATE trailnote_admin.tn_admin_user
      SET status = :status,
          updated_at = NOW()
      WHERE id = :id
      """;

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public JdbcAdminUserRepository(
      JdbcTemplate jdbcTemplate,
      NamedParameterJdbcTemplate namedParameterJdbcTemplate
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public long countAll() {
    Long count = jdbcTemplate.queryForObject(COUNT_SQL, Long.class);
    return count == null ? 0L : count;
  }

  public List<AdminUserRow> list(int limit, int offset) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(LIST_SQL, limit, offset);
    return rows.stream().map(this::toRow).toList();
  }

  public AdminUserRow findById(Long id) {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(DETAIL_SQL, id);
      return toRow(row);
    } catch (EmptyResultDataAccessException exception) {
      throw new IllegalArgumentException("admin user not found: " + id);
    }
  }

  public AdminUserRow findByUsername(String username) {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(DETAIL_BY_USERNAME_SQL, username);
      return toRow(row);
    } catch (EmptyResultDataAccessException exception) {
      throw new IllegalArgumentException("admin user not found: " + username);
    }
  }

  public AdminUserRow findFirstActive() {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(FIRST_ACTIVE_SQL);
      return toRow(row);
    } catch (EmptyResultDataAccessException exception) {
      throw new IllegalArgumentException("no active admin user found");
    }
  }

  public AdminUserSummary summary() {
    Map<String, Object> row = jdbcTemplate.queryForMap(SUMMARY_SQL);
    return new AdminUserSummary(
        toLong(row.get("total_users")),
        toLong(row.get("active_users")),
        toLong(row.get("disabled_users"))
    );
  }

  public int updateStatus(Long id, String status) {
    return namedParameterJdbcTemplate.update(
        UPDATE_STATUS_SQL,
        new MapSqlParameterSource()
            .addValue("status", status)
            .addValue("id", id)
    );
  }

  private AdminUserRow toRow(Map<String, Object> row) {
    return new AdminUserRow(
        toLong(row.get("id")),
        (String) row.get("username"),
        (String) row.get("role_code"),
        (String) row.get("status"),
        toDateTime(row.get("created_at")),
        toDateTime(row.get("updated_at"))
    );
  }

  private static long toLong(Object value) {
    if (value instanceof Number number) {
      return number.longValue();
    }
    return 0L;
  }

  private static LocalDateTime toDateTime(Object value) {
    if (value instanceof Timestamp ts) {
      return ts.toLocalDateTime();
    }
    if (value instanceof LocalDateTime ldt) {
      return ldt;
    }
    return null;
  }
}
