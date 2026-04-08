package com.trailnote.api.interaction.interfaces.http;

import com.trailnote.api.interaction.domain.model.CommentCreateResult;
import com.trailnote.api.interaction.domain.model.FavoriteToggleResult;
import com.trailnote.api.interaction.domain.model.RouteCommentItem;
import com.trailnote.api.interaction.application.RouteCommentService;
import com.trailnote.api.interaction.application.RouteFavoriteService;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/routes")
public class RouteInteractionController {

  private final RouteFavoriteService routeFavoriteService;
  private final RouteCommentService routeCommentService;

  public RouteInteractionController(
      RouteFavoriteService routeFavoriteService,
      RouteCommentService routeCommentService
  ) {
    this.routeFavoriteService = routeFavoriteService;
    this.routeCommentService = routeCommentService;
  }

  @PostMapping("/{routeId}/favorite")
  public ApiResponse<FavoriteToggleResult> toggleFavorite(@PathVariable Long routeId) {
    return ApiResponse.success(routeFavoriteService.toggleFavorite(routeId));
  }

  @GetMapping("/{routeId}/comments")
  public ApiResponse<PageResponse<RouteCommentItem>> pageComments(
      @PathVariable Long routeId,
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "20") long pageSize
  ) {
    return ApiResponse.success(routeCommentService.pageComments(routeId, page, pageSize));
  }

  @PostMapping("/{routeId}/comments")
  public ApiResponse<CommentCreateResult> addComment(
      @PathVariable Long routeId,
      @Valid @RequestBody CreateCommentRequest request
  ) {
    return ApiResponse.success(routeCommentService.addComment(routeId, request.content()));
  }

  public record CreateCommentRequest(
      @NotBlank(message = "must not be blank")
      @Size(max = 500, message = "must be at most 500 characters")
      String content
  ) {
  }
}
