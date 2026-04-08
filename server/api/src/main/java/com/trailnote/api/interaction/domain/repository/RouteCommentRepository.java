package com.trailnote.api.interaction.domain.repository;

import com.trailnote.api.interaction.domain.model.CommentCreateResult;
import com.trailnote.api.interaction.domain.model.RouteCommentItem;
import com.trailnote.common.api.PageResponse;

public interface RouteCommentRepository {
  PageResponse<RouteCommentItem> pageComments(Long routeId, Long currentUserId, long page, long pageSize);
  CommentCreateResult addComment(Long routeId, Long currentUserId, String content);
}
