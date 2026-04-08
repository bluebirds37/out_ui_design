package com.trailnote.admin.auth.support;

import com.trailnote.admin.auth.application.AdminSessionService;
import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminTokenInterceptor implements HandlerInterceptor {

  private final AdminCurrentUserProvider currentUserProvider;
  private final AdminSessionService adminSessionService;

  public AdminTokenInterceptor(
      AdminCurrentUserProvider currentUserProvider,
      AdminSessionService adminSessionService
  ) {
    this.currentUserProvider = currentUserProvider;
    this.adminSessionService = adminSessionService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    AuthenticatedAdmin admin = adminSessionService.resolveCurrentUser(request.getHeader("Authorization"));
    currentUserProvider.setCurrentUser(admin);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception ex
  ) {
    currentUserProvider.clear();
  }
}
