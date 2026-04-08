package com.trailnote.api.social.domain.model;

public record FollowToggleResult(
    Boolean followed,
    Integer followerCount
) {
}
