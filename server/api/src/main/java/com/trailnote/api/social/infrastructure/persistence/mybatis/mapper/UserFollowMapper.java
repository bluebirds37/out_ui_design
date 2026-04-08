package com.trailnote.api.social.infrastructure.persistence.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trailnote.api.social.infrastructure.persistence.mybatis.entity.UserFollowEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollowEntity> {
}
