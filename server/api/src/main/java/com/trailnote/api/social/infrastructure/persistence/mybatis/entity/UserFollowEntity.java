package com.trailnote.api.social.infrastructure.persistence.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("tn_user_follow")
public class UserFollowEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long followerUserId;
  private Long targetUserId;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getFollowerUserId() { return followerUserId; }
  public void setFollowerUserId(Long followerUserId) { this.followerUserId = followerUserId; }
  public Long getTargetUserId() { return targetUserId; }
  public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
