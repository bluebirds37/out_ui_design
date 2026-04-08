package com.trailnote.api.interaction.application;

import com.trailnote.api.interaction.domain.model.CommentCreateResult;
import com.trailnote.api.interaction.domain.model.RouteCommentItem;
import com.trailnote.api.interaction.domain.repository.RouteCommentRepository;
import com.trailnote.api.support.DemoCurrentUserProvider;
import com.trailnote.common.api.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteCommentService {

  private final RouteCommentRepository repository;
  private final DemoCurrentUserProvider currentUserProvider;

  public RouteCommentService(
      RouteCommentRepository repository,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.currentUserProvider = currentUserProvider;
  }

  public PageResponse<RouteCommentItem> pageComments(Long routeId, long page, long pageSize) {
    return repository.pageComments(routeId, currentUserProvider.getCurrentUserId(), page, pageSize);
  }

  @Transactional
  public CommentCreateResult addComment(Long routeId, String content) {
    return repository.addComment(routeId, currentUserProvider.getCurrentUserId(), content);
  }
}
