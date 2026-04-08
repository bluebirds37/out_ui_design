package com.trailnote.admin.comment.interfaces.http;

import com.trailnote.admin.auth.application.AdminAuthorizationService;
import com.trailnote.admin.comment.application.AdminCommentQueryService;
import com.trailnote.admin.comment.domain.model.AdminCommentRow;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {

  private final AdminAuthorizationService adminAuthorizationService;
  private final AdminCommentQueryService queryService;

  public AdminCommentController(
      AdminAuthorizationService adminAuthorizationService,
      AdminCommentQueryService queryService
  ) {
    this.adminAuthorizationService = adminAuthorizationService;
    this.queryService = queryService;
  }

  @GetMapping
  public ApiResponse<PageResponse<AdminCommentRow>> page(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long pageSize
  ) {
    adminAuthorizationService.requirePermission("comment:view");
    return ApiResponse.success(queryService.page(page, pageSize));
  }
}
