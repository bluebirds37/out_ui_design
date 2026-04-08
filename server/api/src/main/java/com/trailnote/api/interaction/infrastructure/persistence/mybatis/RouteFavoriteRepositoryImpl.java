package com.trailnote.api.interaction.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trailnote.api.interaction.domain.model.FavoriteToggleResult;
import com.trailnote.api.interaction.domain.repository.RouteFavoriteRepository;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.entity.RouteFavoriteEntity;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.mapper.RouteFavoriteMapper;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMapper;
import com.trailnote.api.user.infrastructure.persistence.mybatis.entity.UserEntity;
import com.trailnote.api.user.infrastructure.persistence.mybatis.mapper.UserMapper;
import com.trailnote.common.api.PageResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RouteFavoriteRepositoryImpl implements RouteFavoriteRepository {

  private final RouteFavoriteMapper routeFavoriteMapper;
  private final RouteMapper routeMapper;
  private final UserMapper userMapper;
  private final WaypointMapper waypointMapper;

  public RouteFavoriteRepositoryImpl(
      RouteFavoriteMapper routeFavoriteMapper,
      RouteMapper routeMapper,
      UserMapper userMapper,
      WaypointMapper waypointMapper
  ) {
    this.routeFavoriteMapper = routeFavoriteMapper;
    this.routeMapper = routeMapper;
    this.userMapper = userMapper;
    this.waypointMapper = waypointMapper;
  }

  @Override
  public FavoriteToggleResult toggleFavorite(Long routeId, Long currentUserId) {
    RouteEntity route = routeMapper.selectById(routeId);
    if (route == null) {
      throw new IllegalArgumentException("route not found: " + routeId);
    }

    RouteFavoriteEntity existing = routeFavoriteMapper.selectOne(
        new LambdaQueryWrapper<RouteFavoriteEntity>()
            .eq(RouteFavoriteEntity::getRouteId, routeId)
            .eq(RouteFavoriteEntity::getUserId, currentUserId)
            .last("LIMIT 1")
    );

    boolean favorited;
    int favoriteCount = safeCount(route.getFavoriteCount());
    if (existing == null) {
      RouteFavoriteEntity favorite = new RouteFavoriteEntity();
      favorite.setRouteId(routeId);
      favorite.setUserId(currentUserId);
      favorite.setCreatedAt(LocalDateTime.now());
      routeFavoriteMapper.insert(favorite);
      favoriteCount += 1;
      favorited = true;
    } else {
      routeFavoriteMapper.deleteById(existing.getId());
      favoriteCount = Math.max(0, favoriteCount - 1);
      favorited = false;
    }

    route.setFavoriteCount(favoriteCount);
    routeMapper.updateById(route);
    return new FavoriteToggleResult(favorited, favoriteCount);
  }

  @Override
  public PageResponse<RouteSummary> pageMyFavorites(Long currentUserId, long page, long pageSize) {
    Page<RouteFavoriteEntity> result = routeFavoriteMapper.selectPage(
        new Page<>(page, pageSize),
        new LambdaQueryWrapper<RouteFavoriteEntity>()
            .eq(RouteFavoriteEntity::getUserId, currentUserId)
            .orderByDesc(RouteFavoriteEntity::getCreatedAt)
            .orderByDesc(RouteFavoriteEntity::getId)
    );

    List<Long> routeIds = result.getRecords().stream().map(RouteFavoriteEntity::getRouteId).toList();
    if (routeIds.isEmpty()) {
      return PageResponse.of(List.of(), result.getTotal(), page, pageSize);
    }

    Map<Long, RouteEntity> routeMap = routeMapper.selectBatchIds(routeIds).stream()
        .collect(Collectors.toMap(RouteEntity::getId, Function.identity()));

    List<RouteEntity> orderedRoutes = routeIds.stream()
        .map(routeMap::get)
        .filter(route -> route != null && RouteLifecycleStatus.PUBLISHED.value().equals(route.getStatus()))
        .toList();

    return PageResponse.of(toRouteSummaries(orderedRoutes), result.getTotal(), page, pageSize);
  }

  @Override
  public boolean isRouteFavoritedByUser(Long routeId, Long userId) {
    Long count = routeFavoriteMapper.selectCount(
        new LambdaQueryWrapper<RouteFavoriteEntity>()
            .eq(RouteFavoriteEntity::getRouteId, routeId)
            .eq(RouteFavoriteEntity::getUserId, userId)
    );
    return count != null && count > 0;
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

  private int safeCount(Integer value) {
    return value == null ? 0 : value;
  }
}
