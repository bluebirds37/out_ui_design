package com.trailnote.admin.auth.domain.repository;

import com.trailnote.admin.auth.domain.model.AdminSession;
import com.trailnote.admin.auth.domain.model.AdminMenuEntry;
import com.trailnote.admin.user.domain.model.AdminUserRow;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminAuthGateway {
  AdminUserRow findUserByUsername(String username);
  AdminUserRow findFirstActiveUser();
  void createSession(Long adminUserId, String accessToken, LocalDateTime expiresAt);
  AdminSession findSessionByAccessToken(String accessToken);
  int revokeSession(String accessToken);
  List<String> findAccessCodesByRoleCode(String roleCode);
  List<AdminMenuEntry> findMenuRowsByRoleCode(String roleCode);
}
