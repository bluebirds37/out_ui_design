package com.trailnote.api.social.application;

import com.trailnote.api.social.domain.model.AuthorProfile;
import com.trailnote.api.social.domain.model.FollowToggleResult;
import com.trailnote.api.social.domain.repository.AuthorRepository;
import com.trailnote.api.support.DemoCurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {

  private final AuthorRepository repository;
  private final DemoCurrentUserProvider currentUserProvider;

  public AuthorService(
      AuthorRepository repository,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.currentUserProvider = currentUserProvider;
  }

  public AuthorProfile getAuthorProfile(Long authorId) {
    return repository.getAuthorProfile(authorId, currentUserProvider.getCurrentUserId());
  }

  @Transactional
  public FollowToggleResult toggleFollow(Long authorId) {
    return repository.toggleFollow(authorId, currentUserProvider.getCurrentUserId());
  }

  public boolean isFollowedByCurrentUser(Long authorId) {
    return repository.isFollowedByUser(authorId, currentUserProvider.getCurrentUserId());
  }
}
