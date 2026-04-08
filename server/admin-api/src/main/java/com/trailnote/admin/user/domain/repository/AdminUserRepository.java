package com.trailnote.admin.user.domain.repository;

import com.trailnote.admin.user.domain.model.AdminUserRow;
import com.trailnote.admin.user.domain.model.AdminUserSummary;
import java.util.List;

public interface AdminUserRepository {

  long countAll();

  List<AdminUserRow> list(int limit, int offset);

  AdminUserRow findById(Long id);

  AdminUserRow findByUsername(String username);

  AdminUserRow findFirstActive();

  AdminUserSummary summary();

  int updateStatus(Long id, String status);
}
