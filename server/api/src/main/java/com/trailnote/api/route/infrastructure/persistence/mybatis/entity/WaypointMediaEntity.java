package com.trailnote.api.route.infrastructure.persistence.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.trailnote.api.route.domain.model.MediaType;
import java.time.LocalDateTime;

@TableName("tn_waypoint_media")
public class WaypointMediaEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long waypointId;
  private MediaType mediaType;
  private String coverUrl;
  private String mediaUrl;
  private Integer durationSeconds;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getWaypointId() { return waypointId; }
  public void setWaypointId(Long waypointId) { this.waypointId = waypointId; }
  public MediaType getMediaType() { return mediaType; }
  public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
  public String getCoverUrl() { return coverUrl; }
  public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
  public String getMediaUrl() { return mediaUrl; }
  public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
  public Integer getDurationSeconds() { return durationSeconds; }
  public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
