package com.trailnote.api.social.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.domain.model.RouteTagSet;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.WaypointMapper;
import com.trailnote.api.social.domain.model.AuthorProfile;
import com.trailnote.api.social.domain.model.FollowToggleResult;
import com.trailnote.api.social.domain.repository.AuthorRepository;
import com.trailnote.api.social.infrastructure.persistence.mybatis.entity.UserFollowEntity;
import com.trailnote.api.social.infrastructure.persistence.mybatis.mapper.UserFollowMapper;
import com.trailnote.api.user.infrastructure.persistence.mybatis.entity.UserEntity;
import com.trailnote.api.user.infrastructure.persistence.mybatis.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepositoryImpl implements AuthorRepository {

  private final UserMapper userMapper;
  private final RouteMapper routeMapper;
  private final WaypointMapper waypointMapper;
  private final UserFollowMapper userFollowMapper;

  public AuthorRepositoryImpl(
      UserMapper userMapper,
      RouteMapper routeMapper,
      WaypointMapper waypointMapper,
      UserFollowMapper userFollowMapper
  ) {
    this.userMapper = userMapper;
    this.routeMapper = routeMapper;
    this.waypointMapper = waypointMapper;
    this.userFollowMapper = userFollowMapper;
  }

  @Override
  public AuthorProfile getAuthorProfile(Long authorId, Long currentUserId) {
    UserEntity author = userMapper.selectById(authorId);
    if (author == null) {
      throw new IllegalArgumentException("author not found: " + authorId);
    }

    List<RouteEntity> routes = routeMapper.selectList(
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getUserId, authorId)
            .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
            .orderByDesc(RouteEntity::getPublishedAt)
            .orderByDesc(RouteEntity::getId)
    );

    Long followerCount = userFollowMapper.selectCount(
        new LambdaQueryWrapper<UserFollowEntity>()
            .eq(UserFollowEntity::getTargetUserId, authorId)
    );
    Long followingCount = userFollowMapper.selectCount(
        new LambdaQueryWrapper<UserFollowEntity>()
            .eq(UserFollowEntity::getFollowerUserId, authorId)
    );

    return new AuthorProfile(
        author.getId(),
        author.getNickname(),
        author.getAvatarUrl(),
        author.getBio(),
        author.getCity(),
        author.getLevelLabel(),
        toInt(followerCount),
        toInt(followingCount),
        routes.size(),
        isFollowedByUser(authorId, currentUserId),
        toRouteSummaries(routes)
    );
  }

  @Override
  public FollowToggleResult toggleFollow(Long authorId, Long currentUserId) {
    if (currentUserId.equals(authorId)) {
      throw new IllegalArgumentException("cannot follow yourself");
    }
    if (userMapper.selectById(authorId) == null) {
      throw new IllegalArgumentException("author not found: " + authorId);
    }

    UserFollowEntity existing = userFollowMapper.selectOne(
        new LambdaQueryWrapper<UserFollowEntity>()
            .eq(UserFollowEntity::getFollowerUserId, currentUserId)
            .eq(UserFollowEntity::getTargetUserId, authorId)
            .last("LIMIT 1")
    );

    boolean followed;
    if (existing == null) {
      UserFollowEntity follow = new UserFollowEntity();
      follow.setFollowerUserId(currentUserId);
      follow.setTargetUserId(authorId);
      follow.setCreatedAt(LocalDateTime.now());
      userFollowMapper.insert(follow);
      followed = true;
    } else {
      userFollowMapper.deleteById(existing.getId());
      followed = false;
    }

    Long followerCount = userFollowMapper.selectCount(
        new LambdaQueryWrapper<UserFollowEntity>()
            .eq(UserFollowEntity::getTargetUserId, authorId)
    );
    return new FollowToggleResult(followed, toInt(followerCount));
  }

  @Override
  public boolean isFollowedByUser(Long authorId, Long currentUserId) {
    Long count = userFollowMapper.selectCount(
        new LambdaQueryWrapper<UserFollowEntity>()
            .eq(UserFollowEntity::getFollowerUserId, currentUserId)
            .eq(UserFollowEntity::getTargetUserId, authorId)
    );
    return count != null && count > 0;
  }

  private List<RouteSummary> toRouteSummaries(List<RouteEntity> routes) {
    if (routes.isEmpty()) {
      return List.of();
    }

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
            userMapper.selectById(route.getUserId()).getNickname(),
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

  private int toInt(Long value) {
    return value == null ? 0 : value.intValue();
  }
}
