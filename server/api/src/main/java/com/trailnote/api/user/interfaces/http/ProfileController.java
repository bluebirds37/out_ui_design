package com.trailnote.api.user.interfaces.http;

import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.interaction.application.RouteFavoriteService;
import com.trailnote.api.user.domain.model.MyProfile;
import com.trailnote.api.user.domain.model.UpdateProfileCommand;
import com.trailnote.api.user.application.ProfileService;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/me")
public class ProfileController {

  private final ProfileService profileService;
  private final RouteFavoriteService routeFavoriteService;

  public ProfileController(
      ProfileService profileService,
      RouteFavoriteService routeFavoriteService
  ) {
    this.profileService = profileService;
    this.routeFavoriteService = routeFavoriteService;
  }

  @GetMapping
  public ApiResponse<MyProfile> profile() {
    return ApiResponse.success(profileService.getMyProfile());
  }

  @PutMapping
  public ApiResponse<MyProfile> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
    return ApiResponse.success(profileService.updateMyProfile(
        new UpdateProfileCommand(
            request.nickname(),
            request.avatarUrl(),
            request.bio(),
            request.city(),
            request.levelLabel()
        )
    ));
  }

  @GetMapping("/favorites")
  public ApiResponse<PageResponse<RouteSummary>> favorites(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long pageSize
  ) {
    return ApiResponse.success(routeFavoriteService.pageMyFavorites(page, pageSize));
  }

  public record UpdateProfileRequest(
      @NotBlank(message = "must not be blank")
      @Size(max = 64, message = "must be at most 64 characters")
      String nickname,
      @Size(max = 255, message = "must be at most 255 characters")
      String avatarUrl,
      @Size(max = 255, message = "must be at most 255 characters")
      String bio,
      @Size(max = 64, message = "must be at most 64 characters")
      String city,
      @Size(max = 64, message = "must be at most 64 characters")
      String levelLabel
  ) {
  }
}
