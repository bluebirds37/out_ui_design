package com.trailnote.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.trailnote.api.creator.domain.model.CreatorRouteDraft;
import com.trailnote.api.creator.domain.model.CreatorRouteDraftSnapshot;
import com.trailnote.api.creator.domain.model.RouteDraftMediaInput;
import com.trailnote.api.creator.domain.model.RouteDraftWaypointInput;
import com.trailnote.api.creator.domain.model.SaveDraftCommand;
import com.trailnote.api.route.domain.model.MediaType;
import com.trailnote.api.route.domain.model.RouteDifficulty;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.domain.model.WaypointType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreatorDomainBehaviorTests {

  @Test
  void creatorRouteDraftRejectsDuplicateWaypointSortOrder() {
    SaveDraftCommand command = new SaveDraftCommand(
        1L,
        "路线",
        "cover.png",
        "desc",
        RouteDifficulty.INTERMEDIATE,
        8.0,
        90,
        200,
        500,
        List.of("湖景", "周末"),
        List.of(
            waypoint(1, "起点"),
            waypoint(1, "终点")
        )
    );

    IllegalArgumentException error =
        assertThrows(IllegalArgumentException.class, () -> CreatorRouteDraft.from(command).validateForSave());

    assertEquals("duplicate waypoint sortOrder: 1", error.getMessage());
  }

  @Test
  void creatorRouteDraftKeepsPublishedStatusOnRepublish() {
    CreatorRouteDraftSnapshot detail = new CreatorRouteDraftSnapshot(
        7L,
        "已发布路线",
        "cover.png",
        "desc",
        RouteDifficulty.ADVANCED,
        12.0,
        180,
        400,
        900,
        RouteLifecycleStatus.PUBLISHED.value(),
        List.of("高山"),
        List.of(waypoint(1, "起点")),
        LocalDateTime.of(2026, 4, 5, 10, 0)
    );

    assertEquals(RouteLifecycleStatus.PUBLISHED.value(), CreatorRouteDraft.from(detail).resolveNextPublishStatus());
  }

  @Test
  void routeTagSetNormalizesAndDeduplicatesTags() {
    RouteTagSet tagSet = RouteTagSet.fromList(List.of(" 湖景 ", "周末", "湖景", "", "  "));

    assertEquals(List.of("湖景", "周末"), tagSet.values());
    assertEquals("湖景,周末", tagSet.toRaw());
  }

  @Test
  void creatorRouteDraftRejectsVideoMediaWithoutDuration() {
    SaveDraftCommand command = new SaveDraftCommand(
        2L,
        "视频路线",
        "cover.png",
        "desc",
        RouteDifficulty.BEGINNER,
        3.0,
        45,
        60,
        120,
        List.of("视频"),
        List.of(new RouteDraftWaypointInput(
            1L,
            "拍摄点",
            WaypointType.PHOTO_SPOT,
            "desc",
            31.0,
            121.0,
            20,
            1,
            List.of(new RouteDraftMediaInput(1L, MediaType.VIDEO, "cover.png", "video.mp4", null))
        ))
    );

    IllegalArgumentException error =
        assertThrows(IllegalArgumentException.class, () -> CreatorRouteDraft.from(command).validateForSave());

    assertEquals("video durationSeconds must be greater than 0", error.getMessage());
  }

  private static RouteDraftWaypointInput waypoint(int sortOrder, String title) {
    return new RouteDraftWaypointInput(
        (long) sortOrder,
        title,
        WaypointType.VIEWPOINT,
        "desc",
        30.0 + sortOrder,
        120.0 + sortOrder,
        100 + sortOrder,
        sortOrder,
        List.of(new RouteDraftMediaInput(
            (long) sortOrder,
            MediaType.PHOTO,
            "cover-" + sortOrder + ".png",
            "media-" + sortOrder + ".png",
            null
        ))
    );
  }
}
