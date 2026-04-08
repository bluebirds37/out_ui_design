package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.MediaType;

public record CreatorDraftMedia(
    Long id,
    MediaType mediaType,
    String coverUrl,
    String mediaUrl,
    Integer durationSeconds
) {

  public static CreatorDraftMedia from(RouteDraftMediaInput input) {
    return new CreatorDraftMedia(
        input.id(),
        input.mediaType(),
        input.coverUrl(),
        input.mediaUrl(),
        input.durationSeconds()
    );
  }

  public void validate() {
    if (mediaType == null) {
      throw new IllegalArgumentException("mediaType must not be null");
    }
    if (mediaUrl == null || mediaUrl.isBlank()) {
      throw new IllegalArgumentException("mediaUrl must not be blank");
    }
    if (mediaType == MediaType.VIDEO && (durationSeconds == null || durationSeconds <= 0)) {
      throw new IllegalArgumentException("video durationSeconds must be greater than 0");
    }
  }
}
