package com.trailnote.api.route.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.entity.RouteFavoriteEntity;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.mapper.RouteFavoriteMapper;
import com.trailnote.api.route.domain.model.RouteAggregate;
import com.trailnote.api.route.domain.model.RouteDetail;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.PublishedRoute;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.domain.model.WaypointMedia;
import com.trailnote.api.route.domain.model.WaypointSummary;
import com.trailnote.api.route.domain.repository.RouteQueryRepository;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointMediaEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMediaMapper;
import com.trailnote.api.user.infrastructure.persistence.mybatis.entity.UserEntity;
import com.trailnote.api.user.infrastructure.persistence.mybatis.mapper.UserMapper;
import com.trailnote.common.api.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RouteQueryRepositoryImpl implements RouteQueryRepository {

  private final RouteMapper routeMapper;
  private final UserMapper userMapper;
  private final WaypointMapper waypointMapper;
  private final WaypointMediaMapper waypointMediaMapper;
  private final RouteFavoriteMapper routeFavoriteMapper;

  public RouteQueryRepositoryImpl(
      RouteMapper routeMapper,
      UserMapper userMapper,
      WaypointMapper waypointMapper,
      WaypointMediaMapper waypointMediaMapper,
      RouteFavoriteMapper routeFavoriteMapper
  ) {
    this.routeMapper = routeMapper;
    this.userMapper = userMapper;
    this.waypointMapper = waypointMapper;
    this.waypointMediaMapper = waypointMediaMapper;
    this.routeFavoriteMapper = routeFavoriteMapper;
  }

  @Override
  public List<RouteSummary> listFeaturedRoutes() {
    List<RouteEntity> routes = routeMapper.selectList(
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
            .orderByDesc(RouteEntity::getFavoriteCount)
            .last("LIMIT 10")
    );
    return toRouteSummaries(routes);
  }

  @Override
  public PageResponse<RouteSummary> page(long page, long pageSize) {
    Page<RouteEntity> result = routeMapper.selectPage(
        new Page<>(page, pageSize),
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
            .orderByDesc(RouteEntity::getPublishedAt)
            .orderByDesc(RouteEntity::getId)
    );
    return PageResponse.of(toRouteSummaries(result.getRecords()), result.getTotal(), page, pageSize);
  }

  @Override
  public RouteDetail getRouteDetail(Long routeId, Long currentUserId) {
    RouteEntity route = routeMapper.selectById(routeId);
    if (route == null) {
      throw new IllegalArgumentException("route not found: " + routeId);
    }

    UserEntity author = userMapper.selectById(route.getUserId());
    List<WaypointEntity> waypointEntities = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .eq(WaypointEntity::getRouteId, routeId)
            .orderByAsc(WaypointEntity::getSortOrder)
            .orderByAsc(WaypointEntity::getId)
    );

    Map<Long, List<WaypointMediaEntity>> mediaMap = loadWaypointMediaMap(waypointEntities);

    List<WaypointSummary> waypoints = waypointEntities.stream()
        .map(entity -> new WaypointSummary(
            entity.getId(),
            entity.getTitle(),
            entity.getWaypointType(),
            entity.getDescription(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getAltitudeM(),
            entity.getSortOrder(),
            mediaMap.getOrDefault(entity.getId(), List.of()).stream()
                .map(this::toWaypointMedia)
                .toList()
        ))
        .toList();

    PublishedRoute publishedRoute = toAggregate(route).toPublishedRoute();
    return publishedRoute.toDetail(
        author == null ? "未知作者" : author.getNickname(),
        isFavoritedByUser(route.getId(), currentUserId),
        waypoints
    );
  }

  private List<RouteSummary> toRouteSummaries(List<RouteEntity> routes) {
    if (routes.isEmpty()) {
      return List.of();
    }

    List<Long> userIds = routes.stream()
        .map(RouteEntity::getUserId)
        .distinct()
        .toList();

    Map<Long, UserEntity> userMap = userMapper.selectBatchIds(userIds).stream()
        .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

    Map<Long, Long> waypointCountMap = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .in(WaypointEntity::getRouteId, routes.stream().map(RouteEntity::getId).toList())
    ).stream().collect(Collectors.groupingBy(WaypointEntity::getRouteId, Collectors.counting()));

    return routes.stream()
        .map(route -> toAggregate(route).toPublishedRoute().toSummary(
            userMap.containsKey(route.getUserId()) ? userMap.get(route.getUserId()).getNickname() : "未知作者",
            waypointCountMap.getOrDefault(route.getId(), 0L).intValue()
        ))
        .toList();
  }

  private Map<Long, List<WaypointMediaEntity>> loadWaypointMediaMap(List<WaypointEntity> waypointEntities) {
    if (waypointEntities.isEmpty()) {
      return Collections.emptyMap();
    }
    List<Long> waypointIds = waypointEntities.stream().map(WaypointEntity::getId).toList();
    return waypointMediaMapper.selectList(
        new LambdaQueryWrapper<WaypointMediaEntity>()
            .in(WaypointMediaEntity::getWaypointId, waypointIds)
            .orderByAsc(WaypointMediaEntity::getId)
    ).stream().collect(Collectors.groupingBy(WaypointMediaEntity::getWaypointId));
  }

  private WaypointMedia toWaypointMedia(WaypointMediaEntity entity) {
    return new WaypointMedia(
        entity.getId(),
        entity.getMediaType(),
        entity.getCoverUrl(),
        entity.getMediaUrl(),
        entity.getDurationSeconds()
    );
  }

  private boolean isFavoritedByUser(Long routeId, Long userId) {
    Long count = routeFavoriteMapper.selectCount(
        new LambdaQueryWrapper<RouteFavoriteEntity>()
            .eq(RouteFavoriteEntity::getRouteId, routeId)
            .eq(RouteFavoriteEntity::getUserId, userId)
    );
    return count != null && count > 0;
  }

  private RouteAggregate toAggregate(RouteEntity route) {
    return RouteAggregate.of(
        route.getId(),
        route.getTitle(),
        route.getCoverUrl(),
        route.getDescription(),
        route.getUserId(),
        route.getDifficulty(),
        route.getDistanceKm(),
        route.getDurationMinutes(),
        route.getAscentM(),
        route.getMaxAltitudeM(),
        route.getFavoriteCount(),
        route.getCommentCount(),
        RouteTagSet.fromRaw(route.getTags()),
        RouteLifecycleStatus.fromValue(route.getStatus())
    );
  }

}
