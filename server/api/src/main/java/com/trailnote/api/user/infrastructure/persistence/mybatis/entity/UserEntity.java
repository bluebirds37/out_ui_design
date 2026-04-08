package com.trailnote.api.user.infrastructure.persistence.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("tn_user")
public class UserEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String nickname;
  private String avatarUrl;
  private String bio;
  private String city;
  private String levelLabel;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getNickname() { return nickname; }
  public void setNickname(String nickname) { this.nickname = nickname; }
  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
  public String getBio() { return bio; }
  public void setBio(String bio) { this.bio = bio; }
  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }
  public String getLevelLabel() { return levelLabel; }
  public void setLevelLabel(String levelLabel) { this.levelLabel = levelLabel; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
