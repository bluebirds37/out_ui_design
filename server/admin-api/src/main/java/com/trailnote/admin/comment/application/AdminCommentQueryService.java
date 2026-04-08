package com.trailnote.admin.comment.application;

import com.trailnote.admin.comment.domain.model.AdminCommentRow;
import com.trailnote.admin.comment.domain.repository.AdminCommentQueryRepository;
import com.trailnote.common.api.PageResponse;
import org.springframework.stereotype.Service;

@Service
public class AdminCommentQueryService {

  private final AdminCommentQueryRepository repository;

  public AdminCommentQueryService(AdminCommentQueryRepository repository) {
    this.repository = repository;
  }

  public PageResponse<AdminCommentRow> page(long page, long pageSize) {
    return repository.page(page, pageSize);
  }
}
