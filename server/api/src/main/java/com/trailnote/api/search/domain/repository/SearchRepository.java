package com.trailnote.api.search.domain.repository;

import com.trailnote.api.search.domain.model.MapRouteItem;
import com.trailnote.api.search.domain.model.SearchResult;
import java.util.List;

public interface SearchRepository {
  SearchResult search(String keyword, Long currentUserId);
  List<MapRouteItem> mapRoutes(String keyword);
}
