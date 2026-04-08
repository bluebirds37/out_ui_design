package com.trailnote.api.search.domain.model;

public record SearchAuthorItem(
    Long id,
    String nickname,
    String avatarUrl,
    String bio,
    String city,
    String levelLabel,
    Integer followerCount,
    Boolean followed
) {
}
