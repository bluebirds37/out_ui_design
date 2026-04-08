package com.trailnote.api.creator.application.view;

import com.trailnote.api.route.domain.model.MediaType;

public record CreatorRouteDraftMediaView(
    Long id,
    MediaType mediaType,
    String coverUrl,
    String mediaUrl,
    Integer durationSeconds
) {
}
