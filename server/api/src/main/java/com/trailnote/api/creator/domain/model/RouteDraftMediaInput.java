package com.trailnote.api.creator.domain.model;

import com.trailnote.api.route.domain.model.MediaType;

public record RouteDraftMediaInput(
    Long id,
    MediaType mediaType,
    String coverUrl,
    String mediaUrl,
    Integer durationSeconds
) {
}
