package com.trailnote.api.creator.domain.repository;

import com.trailnote.api.creator.domain.model.CreatorRouteRow;
import com.trailnote.api.creator.domain.model.CreatorRouteDraft;
import com.trailnote.api.creator.domain.model.CreatorRouteDraftSnapshot;
import com.trailnote.api.creator.domain.model.PublishRouteResult;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.common.api.PageResponse;

public interface CreatorRouteRepository {
  PageResponse<CreatorRouteRow> pageMyRoutes(Long currentUserId, long page, long pageSize);
  CreatorRouteDraftSnapshot getCurrentDraft(Long currentUserId);
  CreatorRouteDraftSnapshot getDraftDetail(Long currentUserId, Long routeId);
  CreatorRouteDraftSnapshot saveDraft(Long currentUserId, CreatorRouteDraft draft);
  PublishRouteResult updateStatus(Long currentUserId, Long routeId, RouteLifecycleStatus status);
}
