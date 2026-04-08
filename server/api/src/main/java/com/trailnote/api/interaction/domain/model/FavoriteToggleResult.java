package com.trailnote.api.interaction.domain.model;

public record FavoriteToggleResult(
    Boolean favorited,
    Integer favoriteCount
) {
}
