package com.trailnote.api.search.interfaces.http;

import com.trailnote.api.search.domain.model.MapRouteItem;
import com.trailnote.api.search.domain.model.SearchResult;
import com.trailnote.api.search.application.SearchService;
import com.trailnote.common.api.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping
  public ApiResponse<SearchResult> search(@RequestParam(defaultValue = "") String q) {
    return ApiResponse.success(searchService.search(q));
  }

  @GetMapping("/map")
  public ApiResponse<List<MapRouteItem>> mapRoutes(@RequestParam(defaultValue = "") String q) {
    return ApiResponse.success(searchService.mapRoutes(q));
  }
}
