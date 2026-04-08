package com.trailnote.api.route.domain.model;

public record WaypointMedia(
    Long id,
    MediaType mediaType,
    String coverUrl,
    String mediaUrl,
    Integer durationSeconds
) {
}
