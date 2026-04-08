package com.trailnote.api.interaction.domain.repository;

import com.trailnote.api.interaction.domain.model.FavoriteToggleResult;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.common.api.PageResponse;

public interface RouteFavoriteRepository {
  FavoriteToggleResult toggleFavorite(Long routeId, Long currentUserId);
  PageResponse<RouteSummary> pageMyFavorites(Long currentUserId, long page, long pageSize);
  boolean isRouteFavoritedByUser(Long routeId, Long userId);
}
