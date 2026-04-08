package com.trailnote.api.search.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMapper;
import com.trailnote.api.search.domain.model.MapRouteItem;
import com.trailnote.api.search.domain.model.SearchAuthorItem;
import com.trailnote.api.search.domain.model.SearchResult;
import com.trailnote.api.search.domain.model.SearchWaypointItem;
import com.trailnote.api.search.domain.repository.SearchRepository;
import com.trailnote.api.social.infrastructure.persistence.mybatis.entity.UserFollowEntity;
import com.trailnote.api.social.infrastructure.persistence.mybatis.mapper.UserFollowMapper;
import com.trailnote.api.user.infrastructure.persistence.mybatis.entity.UserEntity;
import com.trailnote.api.user.infrastructure.persistence.mybatis.mapper.UserMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl implements SearchRepository {

  private final RouteMapper routeMapper;
  private final UserMapper userMapper;
  private final WaypointMapper waypointMapper;
  private final UserFollowMapper userFollowMapper;

  public SearchRepositoryImpl(
      RouteMapper routeMapper,
      UserMapper userMapper,
      WaypointMapper waypointMapper,
      UserFollowMapper userFollowMapper
  ) {
    this.routeMapper = routeMapper;
    this.userMapper = userMapper;
    this.waypointMapper = waypointMapper;
    this.userFollowMapper = userFollowMapper;
  }

  @Override
  public SearchResult search(String keyword, Long currentUserId) {
    String normalized = keyword == null ? "" : keyword.trim();
    if (normalized.isBlank()) {
      return new SearchResult("", List.of(), List.of(), List.of());
    }

    List<RouteEntity> routes = routeMapper.selectList(
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
            .and(wrapper -> wrapper
                .like(RouteEntity::getTitle, normalized)
                .or()
                .like(RouteEntity::getDescription, normalized)
                .or()
                .like(RouteEntity::getTags, normalized))
            .last("LIMIT 10")
    );

    List<UserEntity> authors = userMapper.selectList(
        new LambdaQueryWrapper<UserEntity>()
            .and(wrapper -> wrapper
                .like(UserEntity::getNickname, normalized)
                .or()
                .like(UserEntity::getBio, normalized)
                .or()
                .like(UserEntity::getCity, normalized))
            .last("LIMIT 8")
    );

    List<WaypointEntity> waypoints = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .and(wrapper -> wrapper
                .like(WaypointEntity::getTitle, normalized)
                .or()
                .like(WaypointEntity::getDescription, normalized))
            .last("LIMIT 12")
    );

    return new SearchResult(
        normalized,
        toRouteSummaries(routes),
        toAuthorItems(authors, currentUserId),
        toWaypointItems(waypoints)
    );
  }

  @Override
  public List<MapRouteItem> mapRoutes(String keyword) {
    List<RouteEntity> routes;
    String normalized = keyword == null ? "" : keyword.trim();
    if (normalized.isBlank()) {
      routes = routeMapper.selectList(
          new LambdaQueryWrapper<RouteEntity>()
              .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
              .orderByDesc(RouteEntity::getPublishedAt)
              .last("LIMIT 20")
      );
    } else {
      routes = routeMapper.selectList(
          new LambdaQueryWrapper<RouteEntity>()
              .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
              .and(wrapper -> wrapper
                  .like(RouteEntity::getTitle, normalized)
                  .or()
                  .like(RouteEntity::getTags, normalized)
                  .or()
                  .like(RouteEntity::getDescription, normalized))
              .orderByDesc(RouteEntity::getPublishedAt)
              .last("LIMIT 20")
      );
    }

    if (routes.isEmpty()) {
      return List.of();
    }

    Map<Long, UserEntity> userMap = userMapper.selectBatchIds(
        routes.stream().map(RouteEntity::getUserId).distinct().toList()
    ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

    Map<Long, WaypointEntity> firstWaypointMap = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .in(WaypointEntity::getRouteId, routes.stream().map(RouteEntity::getId).toList())
            .orderByAsc(WaypointEntity::getSortOrder)
            .orderByAsc(WaypointEntity::getId)
    ).stream().collect(Collectors.toMap(
        WaypointEntity::getRouteId,
        Function.identity(),
        (existing, ignored) -> existing,
        LinkedHashMap::new
    ));

    return routes.stream()
        .filter(route -> firstWaypointMap.containsKey(route.getId()))
        .map(route -> {
          WaypointEntity waypoint = firstWaypointMap.get(route.getId());
          UserEntity author = userMap.get(route.getUserId());
          return new MapRouteItem(
              route.getId(),
              route.getTitle(),
              route.getCoverUrl(),
              author == null ? "未知作者" : author.getNickname(),
              route.getDifficulty(),
              route.getDistanceKm(),
              route.getDurationMinutes(),
              route.getFavoriteCount(),
              waypoint.getLatitude(),
              waypoint.getLongitude(),
              RouteTagSet.fromRaw(route.getTags()).values()
          );
        })
        .toList();
  }

  private List<RouteSummary> toRouteSummaries(List<RouteEntity> routes) {
    if (routes.isEmpty()) {
      return List.of();
    }

    Map<Long, UserEntity> userMap = userMapper.selectBatchIds(
        routes.stream().map(RouteEntity::getUserId).distinct().toList()
    ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

    Map<Long, Integer> waypointCountMap = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .in(WaypointEntity::getRouteId, routes.stream().map(RouteEntity::getId).toList())
    ).stream().collect(Collectors.groupingBy(
        WaypointEntity::getRouteId,
        LinkedHashMap::new,
        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
    ));

    return routes.stream()
        .map(route -> new RouteSummary(
            route.getId(),
            route.getTitle(),
            route.getCoverUrl(),
            userMap.containsKey(route.getUserId()) ? userMap.get(route.getUserId()).getNickname() : "未知作者",
            route.getUserId(),
            route.getDifficulty(),
            route.getDistanceKm(),
            route.getDurationMinutes(),
            route.getAscentM(),
            waypointCountMap.getOrDefault(route.getId(), 0),
            route.getFavoriteCount(),
            RouteTagSet.fromRaw(route.getTags()).values()
        ))
        .toList();
  }

  private List<SearchAuthorItem> toAuthorItems(List<UserEntity> authors, Long currentUserId) {
    if (authors.isEmpty()) {
      return List.of();
    }

    Map<Long, Integer> followerCountMap = userFollowMapper.selectList(
        new LambdaQueryWrapper<UserFollowEntity>()
            .in(UserFollowEntity::getTargetUserId, authors.stream().map(UserEntity::getId).toList())
    ).stream().collect(Collectors.groupingBy(
        UserFollowEntity::getTargetUserId,
        LinkedHashMap::new,
        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
    ));

    return authors.stream()
        .map(author -> new SearchAuthorItem(
            author.getId(),
            author.getNickname(),
            author.getAvatarUrl(),
            author.getBio(),
            author.getCity(),
            author.getLevelLabel(),
            followerCountMap.getOrDefault(author.getId(), 0),
            isFollowed(currentUserId, author.getId())
        ))
        .toList();
  }

  private List<SearchWaypointItem> toWaypointItems(List<WaypointEntity> waypoints) {
    if (waypoints.isEmpty()) {
      return List.of();
    }

    Map<Long, RouteEntity> routeMap = routeMapper.selectBatchIds(
        waypoints.stream().map(WaypointEntity::getRouteId).distinct().toList()
    ).stream().collect(Collectors.toMap(RouteEntity::getId, Function.identity()));

    return waypoints.stream()
        .map(waypoint -> new SearchWaypointItem(
            waypoint.getId(),
            waypoint.getRouteId(),
            routeMap.containsKey(waypoint.getRouteId()) ? routeMap.get(waypoint.getRouteId()).getTitle() : "未知路线",
            waypoint.getTitle(),
            waypoint.getWaypointType().name(),
            waypoint.getDescription(),
            waypoint.getLatitude(),
            waypoint.getLongitude()
        ))
        .toList();
  }

  private boolean isFollowed(Long followerUserId, Long targetUserId) {
    Long count = userFollowMapper.selectCount(
        new LambdaQueryWrapper<UserFollowEntity>()
            .eq(UserFollowEntity::getFollowerUserId, followerUserId)
            .eq(UserFollowEntity::getTargetUserId, targetUserId)
    );
    return count != null && count > 0;
  }

}
