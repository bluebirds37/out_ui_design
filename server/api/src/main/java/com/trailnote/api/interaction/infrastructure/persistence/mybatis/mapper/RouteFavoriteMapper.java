package com.trailnote.api.interaction.infrastructure.persistence.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trailnote.api.interaction.infrastructure.persistence.mybatis.entity.RouteFavoriteEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RouteFavoriteMapper extends BaseMapper<RouteFavoriteEntity> {
}
