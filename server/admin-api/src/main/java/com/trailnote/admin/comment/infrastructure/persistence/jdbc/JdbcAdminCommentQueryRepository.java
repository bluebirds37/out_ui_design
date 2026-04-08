package com.trailnote.admin.comment.infrastructure.persistence.jdbc;

import com.trailnote.admin.comment.domain.model.AdminCommentRow;
import com.trailnote.admin.comment.domain.repository.AdminCommentQueryRepository;
import com.trailnote.common.api.PageResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAdminCommentQueryRepository implements AdminCommentQueryRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcAdminCommentQueryRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public PageResponse<AdminCommentRow> page(long page, long pageSize) {
    long safePage = Math.max(1, page);
    long safePageSize = Math.max(1, pageSize);
    long offset = Math.max(0, (safePage - 1) * safePageSize);

    Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trailnote.tn_route_comment", Long.class);
    List<AdminCommentRow> rows = jdbcTemplate.query(
        """
            SELECT
              c.id,
              c.route_id,
              r.title AS route_title,
              c.user_id,
              u.nickname AS author_name,
              c.content,
              c.created_at
            FROM trailnote.tn_route_comment c
            LEFT JOIN trailnote.tn_route r ON r.id = c.route_id
            LEFT JOIN trailnote.tn_user u ON u.id = c.user_id
            ORDER BY c.created_at DESC, c.id DESC
            LIMIT ? OFFSET ?
            """,
        (rs, rowNum) -> toRow(rs),
        safePageSize,
        offset
    );
    return PageResponse.of(rows, total == null ? 0 : total, safePage, safePageSize);
  }

  private AdminCommentRow toRow(ResultSet resultSet) throws SQLException {
    return new AdminCommentRow(
        resultSet.getLong("id"),
        resultSet.getLong("route_id"),
        resultSet.getString("route_title"),
        resultSet.getLong("user_id"),
        resultSet.getString("author_name"),
        resultSet.getString("content"),
        resultSet.getTimestamp("created_at").toLocalDateTime()
    );
  }
}
