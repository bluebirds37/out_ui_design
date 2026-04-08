package com.trailnote.api.user.domain.repository;

import com.trailnote.api.user.domain.model.MyProfile;
import com.trailnote.api.user.domain.model.UpdateProfileCommand;

public interface ProfileRepository {
  MyProfile getMyProfile(Long currentUserId);
  MyProfile updateMyProfile(Long currentUserId, UpdateProfileCommand command);
}
