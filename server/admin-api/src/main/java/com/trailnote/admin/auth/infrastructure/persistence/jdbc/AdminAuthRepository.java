package com.trailnote.admin.auth.infrastructure.persistence.jdbc;

import com.trailnote.admin.auth.domain.model.AdminSession;
import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import com.trailnote.admin.auth.domain.model.AdminMenuEntry;
import com.trailnote.admin.auth.domain.repository.AdminAuthGateway;
import com.trailnote.admin.user.domain.model.AdminUserRow;
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
public class AdminAuthRepository implements AdminAuthGateway {

  private static final String FIND_USER_BY_USERNAME_SQL = """
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

  private static final String FIND_ACTIVE_DEFAULT_USER_SQL = """
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

  private static final String INSERT_SESSION_SQL = """
      INSERT INTO trailnote_admin.tn_admin_session (
        admin_user_id,
        access_token,
        expires_at
      ) VALUES (
        :adminUserId,
        :accessToken,
        :expiresAt
      )
      """;

  private static final String FIND_SESSION_BY_TOKEN_SQL = """
      SELECT
        s.id AS session_id,
        s.access_token,
        s.expires_at,
        u.id AS admin_user_id,
        u.username,
        u.role_code,
        u.status
      FROM trailnote_admin.tn_admin_session s
      INNER JOIN trailnote_admin.tn_admin_user u ON u.id = s.admin_user_id
      WHERE s.access_token = ?
        AND s.revoked_at IS NULL
      LIMIT 1
      """;

  private static final String REVOKE_SESSION_SQL = """
      UPDATE trailnote_admin.tn_admin_session
      SET revoked_at = NOW(),
          updated_at = NOW()
      WHERE access_token = :accessToken
        AND revoked_at IS NULL
      """;

  private static final String ACCESS_CODES_SQL = """
      SELECT DISTINCT p.permission_code
      FROM trailnote_admin.tn_admin_role_permission rp
      INNER JOIN trailnote_admin.tn_admin_permission p ON p.id = rp.permission_id
      WHERE rp.role_code = ?
      ORDER BY p.permission_code ASC
      """;

  private static final String MENU_ROWS_SQL = """
      SELECT DISTINCT
        m.id,
        m.parent_id,
        m.route_name,
        m.route_path,
        m.component,
        m.redirect,
        m.title,
        m.icon,
        m.menu_order,
        m.affix_tab
      FROM trailnote_admin.tn_admin_role_menu rm
      INNER JOIN trailnote_admin.tn_admin_menu m ON m.id = rm.menu_id
      WHERE rm.role_code = ?
      ORDER BY m.menu_order ASC, m.id ASC
      """;

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public AdminAuthRepository(
      JdbcTemplate jdbcTemplate,
      NamedParameterJdbcTemplate namedParameterJdbcTemplate
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public AdminUserRow findUserByUsername(String username) {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(FIND_USER_BY_USERNAME_SQL, username);
      return toAdminUser(row);
    } catch (EmptyResultDataAccessException exception) {
      throw new IllegalArgumentException("admin user not found: " + username);
    }
  }

  @Override
  public AdminUserRow findFirstActiveUser() {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(FIND_ACTIVE_DEFAULT_USER_SQL);
      return toAdminUser(row);
    } catch (EmptyResultDataAccessException exception) {
      throw new IllegalArgumentException("no active admin user found");
    }
  }

  @Override
  public void createSession(Long adminUserId, String accessToken, LocalDateTime expiresAt) {
    namedParameterJdbcTemplate.update(
        INSERT_SESSION_SQL,
        new MapSqlParameterSource()
            .addValue("adminUserId", adminUserId)
            .addValue("accessToken", accessToken)
            .addValue("expiresAt", Timestamp.valueOf(expiresAt))
    );
  }

  @Override
  public AdminSession findSessionByAccessToken(String accessToken) {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(FIND_SESSION_BY_TOKEN_SQL, accessToken);
      return new AdminSession(
          toLong(row.get("session_id")),
          (String) row.get("access_token"),
          toDateTime(row.get("expires_at")),
          new AuthenticatedAdmin(
              toLong(row.get("admin_user_id")),
              (String) row.get("username"),
              (String) row.get("role_code"),
              (String) row.get("status")
          )
      );
    } catch (EmptyResultDataAccessException exception) {
      throw new IllegalArgumentException("admin session not found");
    }
  }

  @Override
  public int revokeSession(String accessToken) {
    return namedParameterJdbcTemplate.update(
        REVOKE_SESSION_SQL,
        new MapSqlParameterSource("accessToken", accessToken)
    );
  }

  @Override
  public List<String> findAccessCodesByRoleCode(String roleCode) {
    return jdbcTemplate.queryForList(ACCESS_CODES_SQL, String.class, roleCode);
  }

  @Override
  public List<AdminMenuEntry> findMenuRowsByRoleCode(String roleCode) {
    return jdbcTemplate.query(
        MENU_ROWS_SQL,
        (rs, rowNum) -> new AdminMenuEntry(
            rs.getLong("id"),
            rs.getObject("parent_id") == null ? null : rs.getLong("parent_id"),
            rs.getString("route_name"),
            rs.getString("route_path"),
            rs.getString("component"),
            rs.getString("redirect"),
            rs.getString("title"),
            rs.getString("icon"),
            rs.getObject("menu_order") == null ? null : rs.getInt("menu_order"),
            rs.getObject("affix_tab") == null ? null : rs.getBoolean("affix_tab")
        ),
        roleCode
    );
  }

  private static AdminUserRow toAdminUser(Map<String, Object> row) {
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
    return value instanceof Number number ? number.longValue() : 0L;
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
