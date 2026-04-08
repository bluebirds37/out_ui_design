package com.trailnote.admin.auth.application;

import com.trailnote.admin.auth.domain.model.AdminSession;
import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import com.trailnote.admin.auth.domain.repository.AdminAuthGateway;
import com.trailnote.admin.user.domain.model.AdminUserRow;
import com.trailnote.common.api.UnauthorizedException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AdminSessionService {

  private static final long SESSION_HOURS = 12;

  private final AdminAuthGateway adminAuthGateway;

  public AdminSessionService(AdminAuthGateway adminAuthGateway) {
    this.adminAuthGateway = adminAuthGateway;
  }

  public AdminLoginSession createSession(String requestedUsername) {
    AdminUserRow adminUser = resolveLoginUser(requestedUsername);
    ensureActive(adminUser);

    String accessToken = "tnadm_" + UUID.randomUUID().toString().replace("-", "");
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_HOURS);
    adminAuthGateway.createSession(adminUser.id(), accessToken, expiresAt);

    return new AdminLoginSession(
        accessToken,
        expiresAt,
        new AuthenticatedAdmin(adminUser.id(), adminUser.username(), adminUser.roleCode(), adminUser.status())
    );
  }

  public AuthenticatedAdmin resolveCurrentUser(String authorizationHeader) {
    String accessToken = extractBearerToken(authorizationHeader);
    AdminSession session = adminAuthGateway.findSessionByAccessToken(accessToken);
    if (session.expiresAt() == null || session.expiresAt().isBefore(LocalDateTime.now())) {
      adminAuthGateway.revokeSession(accessToken);
      throw new UnauthorizedException("admin session expired");
    }
    ensureActive(session.admin());
    return session.admin();
  }

  public void logout(String authorizationHeader) {
    String accessToken = extractBearerToken(authorizationHeader);
    int updated = adminAuthGateway.revokeSession(accessToken);
    if (updated == 0) {
      throw new UnauthorizedException("admin session not found");
    }
  }

  private AdminUserRow resolveLoginUser(String requestedUsername) {
    String normalized = normalizeUsername(requestedUsername);
    if (normalized != null) {
      try {
        return adminAuthGateway.findUserByUsername(normalized);
      } catch (IllegalArgumentException ignored) {
        // Fall through to the default active admin for local development compatibility.
      }
    }
    return adminAuthGateway.findFirstActiveUser();
  }

  private static String normalizeUsername(String requestedUsername) {
    if (requestedUsername == null || requestedUsername.isBlank()) {
      return null;
    }
    String normalized = requestedUsername.trim();
    if ("admin".equalsIgnoreCase(normalized)) {
      return "root_admin";
    }
    return normalized.toLowerCase(Locale.ROOT);
  }

  private static void ensureActive(AdminUserRow adminUser) {
    ensureActive(new AuthenticatedAdmin(adminUser.id(), adminUser.username(), adminUser.roleCode(), adminUser.status()));
  }

  private static void ensureActive(AuthenticatedAdmin admin) {
    if (!"ACTIVE".equalsIgnoreCase(admin.status())) {
      throw new UnauthorizedException("admin user is disabled: " + admin.username());
    }
  }

  private static String extractBearerToken(String authorizationHeader) {
    if (authorizationHeader == null || authorizationHeader.isBlank()) {
      throw new UnauthorizedException("missing admin authorization token");
    }
    String prefix = "Bearer ";
    if (!authorizationHeader.startsWith(prefix) || authorizationHeader.length() <= prefix.length()) {
      throw new UnauthorizedException("invalid admin authorization token");
    }
    return authorizationHeader.substring(prefix.length()).trim();
  }

  public record AdminLoginSession(
      String accessToken,
      LocalDateTime expiresAt,
      AuthenticatedAdmin admin
  ) {
  }
}
