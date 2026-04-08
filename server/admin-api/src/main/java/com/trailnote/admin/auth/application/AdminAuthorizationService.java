package com.trailnote.admin.auth.application;

import com.trailnote.admin.auth.domain.model.AuthenticatedAdmin;
import com.trailnote.admin.auth.support.AdminCurrentUserProvider;
import com.trailnote.common.api.ForbiddenException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationService {

  private final AdminCurrentUserProvider currentUserProvider;
  private final AdminPermissionService adminPermissionService;

  public AdminAuthorizationService(
      AdminCurrentUserProvider currentUserProvider,
      AdminPermissionService adminPermissionService
  ) {
    this.currentUserProvider = currentUserProvider;
    this.adminPermissionService = adminPermissionService;
  }

  public void requirePermission(String permissionCode) {
    AuthenticatedAdmin admin = currentUserProvider.getCurrentUser();
    Set<String> accessCodes = new HashSet<>(adminPermissionService.getAccessCodes(admin.roleCode()));
    if (!accessCodes.contains(permissionCode)) {
      throw new ForbiddenException("missing permission: " + permissionCode);
    }
  }
}
