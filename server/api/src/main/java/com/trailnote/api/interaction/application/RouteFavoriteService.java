package com.trailnote.api.interaction.application;

import com.trailnote.api.interaction.domain.model.FavoriteToggleResult;
import com.trailnote.api.interaction.domain.repository.RouteFavoriteRepository;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.support.DemoCurrentUserProvider;
import com.trailnote.common.api.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteFavoriteService {

  private final RouteFavoriteRepository repository;
  private final DemoCurrentUserProvider currentUserProvider;

  public RouteFavoriteService(
      RouteFavoriteRepository repository,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.currentUserProvider = currentUserProvider;
  }

  @Transactional
  public FavoriteToggleResult toggleFavorite(Long routeId) {
    return repository.toggleFavorite(routeId, currentUserProvider.getCurrentUserId());
  }

  public PageResponse<RouteSummary> pageMyFavorites(long page, long pageSize) {
    return repository.pageMyFavorites(currentUserProvider.getCurrentUserId(), page, pageSize);
  }

  public boolean isRouteFavoritedByUser(Long routeId, Long userId) {
    return repository.isRouteFavoritedByUser(routeId, userId);
  }
}
