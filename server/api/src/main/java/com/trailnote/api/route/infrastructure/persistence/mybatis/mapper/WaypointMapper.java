package com.trailnote.api.route.infrastructure.persistence.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trailnote.api.route.infrastructure.persistence.mybatis.entity.WaypointEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WaypointMapper extends BaseMapper<WaypointEntity> {
}
