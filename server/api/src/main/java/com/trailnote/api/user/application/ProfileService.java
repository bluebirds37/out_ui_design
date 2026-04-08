package com.trailnote.api.user.application;

import com.trailnote.api.support.DemoCurrentUserProvider;
import com.trailnote.api.user.domain.model.MyProfile;
import com.trailnote.api.user.domain.model.UpdateProfileCommand;
import com.trailnote.api.user.domain.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

  private final ProfileRepository repository;
  private final DemoCurrentUserProvider currentUserProvider;

  public ProfileService(
      ProfileRepository repository,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.currentUserProvider = currentUserProvider;
  }

  public MyProfile getMyProfile() {
    return repository.getMyProfile(currentUserProvider.getCurrentUserId());
  }

  @Transactional
  public MyProfile updateMyProfile(UpdateProfileCommand command) {
    return repository.updateMyProfile(currentUserProvider.getCurrentUserId(), command);
  }
}
