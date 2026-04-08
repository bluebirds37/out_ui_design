package com.trailnote.admin.route.domain.repository;

import com.trailnote.admin.route.domain.model.AdminRouteDetail;
import com.trailnote.admin.route.domain.model.AdminRouteRow;
import com.trailnote.admin.route.domain.model.AdminRouteStatusResult;
import com.trailnote.common.api.PageResponse;

public interface AdminRouteManagementRepository {
  PageResponse<AdminRouteRow> page(long page, long pageSize);
  AdminRouteDetail detail(Long routeId);
  AdminRouteStatusResult updateStatus(Long routeId, String status);
}
