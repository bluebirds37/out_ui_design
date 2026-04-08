package com.trailnote.admin.auth.support;

import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import com.trailnote.common.api.UnauthorizedException;
import org.springframework.stereotype.Component;

@Component
public class AdminCurrentUserProvider {

  private static final ThreadLocal<AuthenticatedAdmin> CURRENT_USER = new ThreadLocal<>();

  public AuthenticatedAdmin getCurrentUser() {
    AuthenticatedAdmin admin = CURRENT_USER.get();
    if (admin == null) {
      throw new UnauthorizedException("admin login required");
    }
    return admin;
  }

  public void setCurrentUser(AuthenticatedAdmin admin) {
    CURRENT_USER.set(admin);
  }

  public void clear() {
    CURRENT_USER.remove();
  }
}
