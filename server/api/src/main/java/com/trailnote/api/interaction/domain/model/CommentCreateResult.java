package com.trailnote.api.interaction.domain.model;

public record CommentCreateResult(
    RouteCommentItem comment,
    Integer commentCount
) {
}
