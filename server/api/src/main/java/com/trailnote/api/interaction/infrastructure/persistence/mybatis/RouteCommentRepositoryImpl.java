package com.trailnote.api.interaction.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trailnote.api.interaction.domain.model.CommentCreateResult;
import com.trailnote.api.interaction.domain.model.RouteCommentItem;
import com.trailnote.api.interaction.domain.repository.RouteCommentRepository;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.entity.RouteCommentEntity;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.mapper.RouteCommentMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.user.infrastructure.persistence.mybatis.entity.UserEntity;
import com.trailnote.api.user.infrastructure.persistence.mybatis.mapper.UserMapper;
import com.trailnote.common.api.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RouteCommentRepositoryImpl implements RouteCommentRepository {

  private final RouteCommentMapper routeCommentMapper;
  private final RouteMapper routeMapper;
  private final UserMapper userMapper;

  public RouteCommentRepositoryImpl(
      RouteCommentMapper routeCommentMapper,
      RouteMapper routeMapper,
      UserMapper userMapper
  ) {
    this.routeCommentMapper = routeCommentMapper;
    this.routeMapper = routeMapper;
    this.userMapper = userMapper;
  }

  @Override
  public PageResponse<RouteCommentItem> pageComments(Long routeId, Long currentUserId, long page, long pageSize) {
    ensureRouteExists(routeId);
    Page<RouteCommentEntity> result = routeCommentMapper.selectPage(
        new Page<>(page, pageSize),
        new LambdaQueryWrapper<RouteCommentEntity>()
            .eq(RouteCommentEntity::getRouteId, routeId)
            .orderByDesc(RouteCommentEntity::getCreatedAt)
            .orderByDesc(RouteCommentEntity::getId)
    );

    return PageResponse.of(toCommentItems(result.getRecords(), currentUserId), result.getTotal(), page, pageSize);
  }

  @Override
  public CommentCreateResult addComment(Long routeId, Long currentUserId, String content) {
    RouteEntity route = ensureRouteExists(routeId);
    LocalDateTime now = LocalDateTime.now();

    RouteCommentEntity entity = new RouteCommentEntity();
    entity.setRouteId(routeId);
    entity.setUserId(currentUserId);
    entity.setContent(content.trim());
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);
    routeCommentMapper.insert(entity);

    route.setCommentCount(safeCount(route.getCommentCount()) + 1);
    routeMapper.updateById(route);

    RouteCommentItem commentItem = toCommentItems(List.of(entity), currentUserId).get(0);
    return new CommentCreateResult(commentItem, route.getCommentCount());
  }

  private RouteEntity ensureRouteExists(Long routeId) {
    RouteEntity route = routeMapper.selectById(routeId);
    if (route == null) {
      throw new IllegalArgumentException("route not found: " + routeId);
    }
    return route;
  }

  private List<RouteCommentItem> toCommentItems(List<RouteCommentEntity> comments, Long currentUserId) {
    if (comments.isEmpty()) {
      return List.of();
    }

    Map<Long, UserEntity> userMap = userMapper.selectBatchIds(
        comments.stream().map(RouteCommentEntity::getUserId).distinct().toList()
    ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

    return comments.stream()
        .map(comment -> {
          UserEntity author = userMap.get(comment.getUserId());
          return new RouteCommentItem(
              comment.getId(),
              comment.getRouteId(),
              comment.getUserId(),
              author == null ? "未知用户" : author.getNickname(),
              author == null ? null : author.getAvatarUrl(),
              comment.getContent(),
              comment.getUserId().equals(currentUserId),
              comment.getCreatedAt()
          );
        })
        .toList();
  }

  private int safeCount(Integer value) {
    return value == null ? 0 : value;
  }
}
