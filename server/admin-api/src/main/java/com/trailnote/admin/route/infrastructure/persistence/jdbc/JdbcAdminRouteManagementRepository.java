package com.trailnote.admin.route.infrastructure.persistence.jdbc;

import com.trailnote.admin.route.domain.model.AdminRouteDetail;
import com.trailnote.admin.route.domain.model.AdminRouteRow;
import com.trailnote.admin.route.domain.model.AdminRouteStatusResult;
import com.trailnote.admin.route.domain.model.AdminRouteWaypointRow;
import com.trailnote.admin.route.domain.repository.AdminRouteManagementRepository;
import com.trailnote.common.api.PageResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAdminRouteManagementRepository implements AdminRouteManagementRepository {

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public JdbcAdminRouteManagementRepository(
      JdbcTemplate jdbcTemplate,
      NamedParameterJdbcTemplate namedParameterJdbcTemplate
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public PageResponse<AdminRouteRow> page(long page, long pageSize) {
    long safePage = Math.max(1, page);
    long safePageSize = Math.max(1, pageSize);
    long offset = Math.max(0, (safePage - 1) * safePageSize);

    Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trailnote.tn_route", Long.class);
    List<AdminRouteRow> rows = jdbcTemplate.query(
        """
            SELECT
              r.id,
              r.title,
              u.nickname AS author_name,
              r.status,
              (
                SELECT COUNT(*)
                FROM trailnote.tn_waypoint w
                WHERE w.route_id = r.id
              ) AS waypoint_count,
              r.favorite_count,
              r.updated_at
            FROM trailnote.tn_route r
            LEFT JOIN trailnote.tn_user u ON u.id = r.user_id
            ORDER BY r.updated_at DESC, r.id DESC
            LIMIT ? OFFSET ?
            """,
        (rs, rowNum) -> toRow(rs),
        safePageSize,
        offset
    );
    return PageResponse.of(rows, total == null ? 0 : total, safePage, safePageSize);
  }

  @Override
  public AdminRouteDetail detail(Long routeId) {
    List<AdminRouteWaypointRow> waypoints = jdbcTemplate.query(
        """
            SELECT
              id,
              title,
              waypoint_type,
              description,
              sort_order
            FROM trailnote.tn_waypoint
            WHERE route_id = ?
            ORDER BY sort_order ASC, id ASC
            """,
        (rs, rowNum) -> new AdminRouteWaypointRow(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("waypoint_type"),
            rs.getString("description"),
            rs.getInt("sort_order")
        ),
        routeId
    );

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        """
            SELECT
              r.id,
              r.title,
              r.description,
              r.status,
              r.difficulty,
              r.distance_km,
              r.duration_minutes,
              r.ascent_m,
              r.max_altitude_m,
              r.favorite_count,
              r.comment_count,
              r.tags,
              r.published_at,
              r.updated_at,
              u.nickname AS author_name
            FROM trailnote.tn_route r
            LEFT JOIN trailnote.tn_user u ON u.id = r.user_id
            WHERE r.id = ?
            """,
        routeId
    );

    if (rows.isEmpty()) {
      throw new IllegalArgumentException("route not found: " + routeId);
    }

    Map<String, Object> row = rows.get(0);
    return new AdminRouteDetail(
        toLong(row.get("id")),
        (String) row.get("title"),
        (String) row.get("author_name"),
        (String) row.get("description"),
        (String) row.get("status"),
        String.valueOf(row.get("difficulty")),
        toDouble(row.get("distance_km")),
        toInteger(row.get("duration_minutes")),
        toInteger(row.get("ascent_m")),
        toInteger(row.get("max_altitude_m")),
        toInteger(row.get("favorite_count")),
        toInteger(row.get("comment_count")),
        (String) row.get("tags"),
        toDateTime(row.get("published_at")),
        toDateTime(row.get("updated_at")),
        waypoints
    );
  }

  @Override
  public AdminRouteStatusResult updateStatus(Long routeId, String status) {
    int updated = namedParameterJdbcTemplate.update(
        """
            UPDATE trailnote.tn_route
            SET status = :status,
                published_at = CASE
                  WHEN :status = 'PUBLISHED' AND published_at IS NULL THEN NOW()
                  ELSE published_at
                END,
                updated_at = NOW()
            WHERE id = :routeId
            """,
        new MapSqlParameterSource()
            .addValue("status", status)
            .addValue("routeId", routeId)
    );

    if (updated == 0) {
      throw new IllegalArgumentException("route not found: " + routeId);
    }

    return namedParameterJdbcTemplate.queryForObject(
        """
            SELECT id, status, published_at, updated_at
            FROM trailnote.tn_route
            WHERE id = :routeId
            """,
        new MapSqlParameterSource("routeId", routeId),
        (rs, rowNum) -> new AdminRouteStatusResult(
            rs.getLong("id"),
            rs.getString("status"),
            rs.getTimestamp("published_at") == null ? null : rs.getTimestamp("published_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        )
    );
  }

  private AdminRouteRow toRow(ResultSet resultSet) throws SQLException {
    return new AdminRouteRow(
        resultSet.getLong("id"),
        resultSet.getString("title"),
        resultSet.getString("author_name"),
        resultSet.getString("status"),
        resultSet.getInt("waypoint_count"),
        resultSet.getInt("favorite_count"),
        resultSet.getTimestamp("updated_at").toLocalDateTime()
    );
  }

  private static long toLong(Object value) {
    return value instanceof Number number ? number.longValue() : 0L;
  }

  private static Integer toInteger(Object value) {
    return value instanceof Number number ? number.intValue() : null;
  }

  private static Double toDouble(Object value) {
    return value instanceof Number number ? number.doubleValue() : null;
  }

  private static LocalDateTime toDateTime(Object value) {
    if (value instanceof java.sql.Timestamp timestamp) {
      return timestamp.toLocalDateTime();
    }
    if (value instanceof LocalDateTime dateTime) {
      return dateTime;
    }
    return null;
  }
}
