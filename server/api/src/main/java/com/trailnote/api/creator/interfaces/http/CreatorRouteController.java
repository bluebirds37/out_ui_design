package com.trailnote.api.creator.interfaces.http;

import com.trailnote.api.creator.application.view.CreatorRouteDraftView;
import com.trailnote.api.creator.domain.model.CreatorRouteRow;
import com.trailnote.api.creator.domain.model.PublishRouteResult;
import com.trailnote.api.creator.domain.model.RouteDraftMediaInput;
import com.trailnote.api.creator.domain.model.RouteDraftWaypointInput;
import com.trailnote.api.creator.domain.model.SaveDraftCommand;
import com.trailnote.api.creator.application.CreatorRouteService;
import com.trailnote.api.route.domain.model.MediaType;
import com.trailnote.api.route.domain.model.RouteDifficulty;
import com.trailnote.api.route.domain.model.WaypointType;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
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
@RequestMapping("/api/creator")
public class CreatorRouteController {

  private final CreatorRouteService creatorRouteService;

  public CreatorRouteController(CreatorRouteService creatorRouteService) {
    this.creatorRouteService = creatorRouteService;
  }

  @GetMapping("/routes")
  public ApiResponse<PageResponse<CreatorRouteRow>> myRoutes(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long pageSize
  ) {
    return ApiResponse.success(creatorRouteService.pageMyRoutes(page, pageSize));
  }

  @GetMapping("/drafts/current")
  public ApiResponse<CreatorRouteDraftView> currentDraft() {
    return ApiResponse.success(creatorRouteService.getCurrentDraft());
  }

  @GetMapping("/drafts/{routeId}")
  public ApiResponse<CreatorRouteDraftView> draftDetail(@PathVariable Long routeId) {
    return ApiResponse.success(creatorRouteService.getDraftDetail(routeId));
  }

  @PostMapping("/drafts/save")
  public ApiResponse<CreatorRouteDraftView> saveDraft(@Valid @RequestBody SaveDraftRequest request) {
    return ApiResponse.success(creatorRouteService.saveDraft(
        new SaveDraftCommand(
            request.routeId(),
            request.title(),
            request.coverUrl(),
            request.description(),
            request.difficulty(),
            request.distanceKm(),
            request.durationMinutes(),
            request.ascentM(),
            request.maxAltitudeM(),
            request.tags(),
            request.waypoints() == null ? List.of() : request.waypoints().stream()
                .map(waypoint -> new RouteDraftWaypointInput(
                    waypoint.id(),
                    waypoint.title(),
                    waypoint.waypointType(),
                    waypoint.description(),
                    waypoint.latitude(),
                    waypoint.longitude(),
                    waypoint.altitudeM(),
                    waypoint.sortOrder(),
                    waypoint.mediaList() == null ? List.of() : waypoint.mediaList().stream()
                        .map(media -> new RouteDraftMediaInput(
                            media.id(),
                            media.mediaType(),
                            media.coverUrl(),
                            media.mediaUrl(),
                            media.durationSeconds()
                        ))
                        .toList()
                ))
                .toList()
        )
    ));
  }

  @PostMapping("/drafts/{routeId}/publish")
  public ApiResponse<PublishRouteResult> publish(@PathVariable Long routeId) {
    return ApiResponse.success(creatorRouteService.publishDraft(routeId));
  }

  public record SaveDraftRequest(
      Long routeId,
      @NotBlank(message = "must not be blank")
      @Size(max = 128, message = "must be at most 128 characters")
      String title,
      @Size(max = 255, message = "must be at most 255 characters")
      String coverUrl,
      @Size(max = 5000, message = "must be at most 5000 characters")
      String description,
      @NotNull(message = "must not be null")
      RouteDifficulty difficulty,
      @NotNull(message = "must not be null")
      Double distanceKm,
      @NotNull(message = "must not be null")
      Integer durationMinutes,
      @NotNull(message = "must not be null")
      Integer ascentM,
      @NotNull(message = "must not be null")
      Integer maxAltitudeM,
      List<String> tags,
      List<SaveDraftWaypointRequest> waypoints
  ) {
  }

  public record SaveDraftWaypointRequest(
      Long id,
      @NotBlank(message = "must not be blank")
      @Size(max = 128, message = "must be at most 128 characters")
      String title,
      @NotNull(message = "must not be null")
      WaypointType waypointType,
      @Size(max = 500, message = "must be at most 500 characters")
      String description,
      @NotNull(message = "must not be null")
      Double latitude,
      @NotNull(message = "must not be null")
      Double longitude,
      Integer altitudeM,
      @NotNull(message = "must not be null")
      Integer sortOrder,
      List<SaveDraftMediaRequest> mediaList
  ) {
  }

  public record SaveDraftMediaRequest(
      Long id,
      @NotNull(message = "must not be null")
      MediaType mediaType,
      @Size(max = 255, message = "must be at most 255 characters")
      String coverUrl,
      @NotBlank(message = "must not be blank")
      @Size(max = 255, message = "must be at most 255 characters")
      String mediaUrl,
      Integer durationSeconds
  ) {
  }
}
