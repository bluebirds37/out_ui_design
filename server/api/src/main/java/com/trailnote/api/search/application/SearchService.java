package com.trailnote.api.search.application;

import com.trailnote.api.search.domain.model.MapRouteItem;
import com.trailnote.api.search.domain.model.SearchResult;
import com.trailnote.api.search.domain.repository.SearchRepository;
import com.trailnote.api.support.DemoCurrentUserProvider;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private final SearchRepository repository;
  private final DemoCurrentUserProvider currentUserProvider;

  public SearchService(
      SearchRepository repository,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.currentUserProvider = currentUserProvider;
  }

  public SearchResult search(String keyword) {
    return repository.search(keyword, currentUserProvider.getCurrentUserId());
  }

  public List<MapRouteItem> mapRoutes(String keyword) {
    return repository.mapRoutes(keyword);
  }
}
