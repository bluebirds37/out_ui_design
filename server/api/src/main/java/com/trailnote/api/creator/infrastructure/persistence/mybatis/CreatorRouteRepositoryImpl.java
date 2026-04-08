package com.trailnote.api.creator.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trailnote.api.creator.domain.model.CreatorRouteRow;
import com.trailnote.api.creator.domain.model.CreatorRouteDraft;
import com.trailnote.api.creator.domain.model.CreatorRouteDraftSnapshot;
import com.trailnote.api.creator.domain.model.CreatorDraftMedia;
import com.trailnote.api.creator.domain.model.CreatorDraftWaypoint;
import com.trailnote.api.creator.domain.model.PublishRouteResult;
import com.trailnote.api.creator.domain.model.RouteDraftMediaInput;
import com.trailnote.api.creator.domain.model.RouteDraftWaypointInput;
import com.trailnote.api.creator.domain.repository.CreatorRouteRepository;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointMediaEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMediaMapper;
import com.trailnote.common.api.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CreatorRouteRepositoryImpl implements CreatorRouteRepository {

  private final RouteMapper routeMapper;
  private final WaypointMapper waypointMapper;
  private final WaypointMediaMapper waypointMediaMapper;

  public CreatorRouteRepositoryImpl(
      RouteMapper routeMapper,
      WaypointMapper waypointMapper,
      WaypointMediaMapper waypointMediaMapper
  ) {
    this.routeMapper = routeMapper;
    this.waypointMapper = waypointMapper;
    this.waypointMediaMapper = waypointMediaMapper;
  }

  @Override
  public PageResponse<CreatorRouteRow> pageMyRoutes(Long currentUserId, long page, long pageSize) {
    Page<RouteEntity> result = routeMapper.selectPage(
        new Page<>(page, pageSize),
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getUserId, currentUserId)
            .orderByDesc(RouteEntity::getUpdatedAt)
            .orderByDesc(RouteEntity::getId)
    );

    return PageResponse.of(toCreatorRows(result.getRecords()), result.getTotal(), page, pageSize);
  }

  @Override
  public CreatorRouteDraftSnapshot getCurrentDraft(Long currentUserId) {
    RouteEntity route = routeMapper.selectOne(
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getUserId, currentUserId)
            .eq(RouteEntity::getStatus, RouteLifecycleStatus.DRAFT.value())
            .orderByDesc(RouteEntity::getUpdatedAt)
            .orderByDesc(RouteEntity::getId)
            .last("LIMIT 1")
    );
    return route == null ? null : toDraftDetail(route);
  }

  @Override
  public CreatorRouteDraftSnapshot getDraftDetail(Long currentUserId, Long routeId) {
    return toDraftDetail(requireOwnedRoute(currentUserId, routeId));
  }

  @Override
  public CreatorRouteDraftSnapshot saveDraft(Long currentUserId, CreatorRouteDraft draft) {
    RouteEntity route =
        draft.routeId() == null ? new RouteEntity() : requireOwnedRoute(currentUserId, draft.routeId());
    boolean creating = route.getId() == null;

    route.setUserId(currentUserId);
    route.setTitle(draft.title());
    route.setCoverUrl(draft.coverUrl());
    route.setDescription(draft.description());
    route.setDifficulty(draft.difficulty());
    route.setDistanceKm(draft.distanceKm());
    route.setDurationMinutes(draft.durationMinutes());
    route.setAscentM(draft.ascentM());
    route.setMaxAltitudeM(draft.maxAltitudeM());
    route.setTags(draft.tags().toRaw());
    route.setStatus(RouteLifecycleStatus.DRAFT.value());

    if (creating) {
      routeMapper.insert(route);
    } else {
      routeMapper.updateById(route);
    }

    replaceWaypoints(route.getId(), draft.waypoints());
    return toDraftDetail(routeMapper.selectById(route.getId()));
  }

  @Override
  public PublishRouteResult updateStatus(Long currentUserId, Long routeId, RouteLifecycleStatus status) {
    RouteEntity route = requireOwnedRoute(currentUserId, routeId);
    route.setStatus(status.value());
    routeMapper.updateById(route);
    RouteEntity updated = routeMapper.selectById(routeId);
    return new PublishRouteResult(updated.getId(), updated.getStatus(), updated.getUpdatedAt());
  }

  private void replaceWaypoints(Long routeId, List<CreatorDraftWaypoint> waypoints) {
    List<WaypointEntity> existingWaypoints = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>().eq(WaypointEntity::getRouteId, routeId)
    );
    if (!existingWaypoints.isEmpty()) {
      List<Long> waypointIds = existingWaypoints.stream().map(WaypointEntity::getId).toList();
      waypointMediaMapper.delete(
          new LambdaQueryWrapper<WaypointMediaEntity>().in(WaypointMediaEntity::getWaypointId, waypointIds)
      );
      waypointMapper.delete(new LambdaQueryWrapper<WaypointEntity>().eq(WaypointEntity::getRouteId, routeId));
    }

    if (waypoints == null || waypoints.isEmpty()) {
      return;
    }

    for (CreatorDraftWaypoint waypointInput : waypoints) {
      WaypointEntity waypoint = new WaypointEntity();
      waypoint.setRouteId(routeId);
      waypoint.setTitle(waypointInput.title());
      waypoint.setWaypointType(waypointInput.waypointType());
      waypoint.setDescription(waypointInput.description());
      waypoint.setLatitude(waypointInput.latitude());
      waypoint.setLongitude(waypointInput.longitude());
      waypoint.setAltitudeM(waypointInput.altitudeM());
      waypoint.setSortOrder(waypointInput.sortOrder());
      waypointMapper.insert(waypoint);

      if (waypointInput.mediaList() == null || waypointInput.mediaList().isEmpty()) {
        continue;
      }

      for (CreatorDraftMedia mediaInput : waypointInput.mediaList()) {
        WaypointMediaEntity media = new WaypointMediaEntity();
        media.setWaypointId(waypoint.getId());
        media.setMediaType(mediaInput.mediaType());
        media.setCoverUrl(mediaInput.coverUrl());
        media.setMediaUrl(mediaInput.mediaUrl());
        media.setDurationSeconds(mediaInput.durationSeconds());
        waypointMediaMapper.insert(media);
      }
    }
  }

  private CreatorRouteDraftSnapshot toDraftDetail(RouteEntity route) {
    List<WaypointEntity> waypointEntities = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .eq(WaypointEntity::getRouteId, route.getId())
            .orderByAsc(WaypointEntity::getSortOrder)
            .orderByAsc(WaypointEntity::getId)
    );
    Map<Long, List<WaypointMediaEntity>> mediaMap = loadMediaMap(waypointEntities);

    return new CreatorRouteDraftSnapshot(
        route.getId(),
        route.getTitle(),
        route.getCoverUrl(),
        route.getDescription(),
        route.getDifficulty(),
        route.getDistanceKm(),
        route.getDurationMinutes(),
        route.getAscentM(),
        route.getMaxAltitudeM(),
        route.getStatus(),
        RouteTagSet.fromRaw(route.getTags()).values(),
        waypointEntities.stream()
            .map(waypoint -> new RouteDraftWaypointInput(
                waypoint.getId(),
                waypoint.getTitle(),
                waypoint.getWaypointType(),
                waypoint.getDescription(),
                waypoint.getLatitude(),
                waypoint.getLongitude(),
                waypoint.getAltitudeM(),
                waypoint.getSortOrder(),
                mediaMap.getOrDefault(waypoint.getId(), List.of()).stream()
                    .map(media -> new RouteDraftMediaInput(
                        media.getId(),
                        media.getMediaType(),
                        media.getCoverUrl(),
                        media.getMediaUrl(),
                        media.getDurationSeconds()
                    ))
                    .toList()
            ))
            .toList(),
        route.getUpdatedAt()
    );
  }

  private List<CreatorRouteRow> toCreatorRows(List<RouteEntity> routes) {
    if (routes.isEmpty()) {
      return List.of();
    }
    Map<Long, Long> waypointCountMap = waypointMapper.selectList(
        new LambdaQueryWrapper<WaypointEntity>()
            .in(WaypointEntity::getRouteId, routes.stream().map(RouteEntity::getId).toList())
    ).stream().collect(Collectors.groupingBy(WaypointEntity::getRouteId, Collectors.counting()));

    return routes.stream()
        .map(route -> new CreatorRouteRow(
            route.getId(),
            route.getTitle(),
            route.getCoverUrl(),
            route.getStatus(),
            route.getDistanceKm(),
            route.getDurationMinutes(),
            waypointCountMap.getOrDefault(route.getId(), 0L).intValue(),
            route.getFavoriteCount(),
            route.getCommentCount(),
            RouteTagSet.fromRaw(route.getTags()).values(),
            route.getUpdatedAt()
        ))
        .toList();
  }

  private Map<Long, List<WaypointMediaEntity>> loadMediaMap(List<WaypointEntity> waypoints) {
    if (waypoints.isEmpty()) {
      return Collections.emptyMap();
    }
    List<Long> waypointIds = waypoints.stream().map(WaypointEntity::getId).toList();
    return waypointMediaMapper.selectList(
        new LambdaQueryWrapper<WaypointMediaEntity>()
            .in(WaypointMediaEntity::getWaypointId, waypointIds)
            .orderByAsc(WaypointMediaEntity::getId)
    ).stream().collect(Collectors.groupingBy(WaypointMediaEntity::getWaypointId));
  }

  private RouteEntity requireOwnedRoute(Long currentUserId, Long routeId) {
    RouteEntity route = routeMapper.selectById(routeId);
    if (route == null) {
      throw new IllegalArgumentException("route not found: " + routeId);
    }
    if (!currentUserId.equals(route.getUserId())) {
      throw new IllegalArgumentException("route does not belong to current user: " + routeId);
    }
    return route;
  }

}
