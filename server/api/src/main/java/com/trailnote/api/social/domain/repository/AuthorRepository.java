package com.trailnote.api.social.domain.repository;

import com.trailnote.api.social.domain.model.AuthorProfile;
import com.trailnote.api.social.domain.model.FollowToggleResult;

public interface AuthorRepository {
  AuthorProfile getAuthorProfile(Long authorId, Long currentUserId);
  FollowToggleResult toggleFollow(Long authorId, Long currentUserId);
  boolean isFollowedByUser(Long authorId, Long currentUserId);
}
