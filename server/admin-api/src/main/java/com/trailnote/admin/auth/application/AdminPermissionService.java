package com.trailnote.admin.auth.application;

import com.trailnote.admin.auth.domain.repository.AdminAuthGateway;
import com.trailnote.admin.auth.domain.model.AdminMenuEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminPermissionService {

  private final AdminAuthGateway adminAuthGateway;

  public AdminPermissionService(AdminAuthGateway adminAuthGateway) {
    this.adminAuthGateway = adminAuthGateway;
  }

  public List<String> getAccessCodes(String roleCode) {
    return adminAuthGateway.findAccessCodesByRoleCode(roleCode);
  }

  public List<AdminMenuNode> getMenus(String roleCode) {
    List<AdminMenuEntry> rows = adminAuthGateway.findMenuRowsByRoleCode(roleCode);
    Map<Long, AdminMenuNode> nodeMap = new LinkedHashMap<>();
    List<AdminMenuNode> roots = new ArrayList<>();

    for (AdminMenuEntry row : rows) {
      Long id = row.id();
      Long parentId = row.parentId();
      AdminMenuNode node = new AdminMenuNode(
          id,
          parentId,
          row.routeName(),
          row.routePath(),
          row.component(),
          row.redirect(),
          new AdminMenuMeta(
              row.title(),
              row.icon(),
              row.menuOrder(),
              row.affixTab()
          ),
          new ArrayList<>()
      );
      nodeMap.put(id, node);
    }

    for (AdminMenuNode node : nodeMap.values()) {
      if (node.parentId() == null) {
        roots.add(node);
        continue;
      }
      AdminMenuNode parent = nodeMap.get(node.parentId());
      if (parent == null) {
        roots.add(node);
        continue;
      }
      parent.children().add(node);
    }

    return roots;
  }

  public record AdminMenuMeta(
      String title,
      String icon,
      Integer order,
      Boolean affixTab
  ) {
  }

  public record AdminMenuNode(
      Long id,
      Long parentId,
      String name,
      String path,
      String component,
      String redirect,
      AdminMenuMeta meta,
      List<AdminMenuNode> children
  ) {
  }

}
