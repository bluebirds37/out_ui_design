package com.trailnote.api.user.domain.model;

public record UpdateProfileCommand(
    String nickname,
    String avatarUrl,
    String bio,
    String city,
    String levelLabel
) {
}
