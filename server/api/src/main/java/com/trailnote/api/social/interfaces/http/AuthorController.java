package com.trailnote.api.social.interfaces.http;

import com.trailnote.api.social.domain.model.AuthorProfile;
import com.trailnote.api.social.domain.model.FollowToggleResult;
import com.trailnote.api.social.application.AuthorService;
import com.trailnote.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

  private final AuthorService authorService;

  public AuthorController(AuthorService authorService) {
    this.authorService = authorService;
  }

  @GetMapping("/{authorId}")
  public ApiResponse<AuthorProfile> profile(@PathVariable Long authorId) {
    return ApiResponse.success(authorService.getAuthorProfile(authorId));
  }

  @PostMapping("/{authorId}/follow")
  public ApiResponse<FollowToggleResult> toggleFollow(@PathVariable Long authorId) {
    return ApiResponse.success(authorService.toggleFollow(authorId));
  }
}
