package com.trailnote.api.user.infrastructure.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.entity.RouteFavoriteEntity;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.mapper.RouteFavoriteMapper;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.RouteEntity;
import com.trailnote.api.route.infrastructure.persistence.mybatis.mapper.RouteMapper;
import com.trailnote.api.user.domain.model.MyProfile;
import com.trailnote.api.user.domain.model.UpdateProfileCommand;
import com.trailnote.api.user.domain.repository.ProfileRepository;
import com.trailnote.api.user.infrastructure.persistence.mybatis.entity.UserEntity;
import com.trailnote.api.user.infrastructure.persistence.mybatis.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

  private final UserMapper userMapper;
  private final RouteMapper routeMapper;
  private final RouteFavoriteMapper routeFavoriteMapper;

  public ProfileRepositoryImpl(
      UserMapper userMapper,
      RouteMapper routeMapper,
      RouteFavoriteMapper routeFavoriteMapper
  ) {
    this.userMapper = userMapper;
    this.routeMapper = routeMapper;
    this.routeFavoriteMapper = routeFavoriteMapper;
  }

  @Override
  public MyProfile getMyProfile(Long currentUserId) {
    UserEntity user = requireCurrentUser(currentUserId);
    Long routeCount = routeMapper.selectCount(
        new LambdaQueryWrapper<RouteEntity>()
            .eq(RouteEntity::getUserId, currentUserId)
            .eq(RouteEntity::getStatus, RouteLifecycleStatus.PUBLISHED.value())
    );
    Long favoriteCount = routeFavoriteMapper.selectCount(
        new LambdaQueryWrapper<RouteFavoriteEntity>()
            .eq(RouteFavoriteEntity::getUserId, currentUserId)
    );

    return new MyProfile(
        user.getId(),
        user.getNickname(),
        user.getAvatarUrl(),
        user.getBio(),
        user.getCity(),
        user.getLevelLabel(),
        routeCount == null ? 0 : routeCount.intValue(),
        favoriteCount == null ? 0 : favoriteCount.intValue()
    );
  }

  @Override
  public MyProfile updateMyProfile(Long currentUserId, UpdateProfileCommand command) {
    UserEntity user = requireCurrentUser(currentUserId);
    user.setNickname(command.nickname());
    user.setAvatarUrl(command.avatarUrl());
    user.setBio(command.bio());
    user.setCity(command.city());
    user.setLevelLabel(command.levelLabel());
    userMapper.updateById(user);
    return getMyProfile(currentUserId);
  }

  private UserEntity requireCurrentUser(Long currentUserId) {
    UserEntity user = userMapper.selectById(currentUserId);
    if (user == null) {
      throw new IllegalArgumentException("current user not found: " + currentUserId);
    }
    return user;
  }
}
