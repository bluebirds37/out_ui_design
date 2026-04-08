package com.trailnote.admin.comment.domain.repository;

import com.trailnote.admin.comment.domain.model.AdminCommentRow;
import com.trailnote.common.api.PageResponse;

public interface AdminCommentQueryRepository {
  PageResponse<AdminCommentRow> page(long page, long pageSize);
}
