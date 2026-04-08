package com.trailnote.api.route.infrastructure.persistence.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.trailnote.api.route.domain.model.RouteDifficulty;
import java.time.LocalDateTime;

@TableName("tn_route")
public class RouteEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long userId;
  private String title;
  private String coverUrl;
  private String description;
  private RouteDifficulty difficulty;
  private Double distanceKm;
  private Integer durationMinutes;
  private Integer ascentM;
  private Integer maxAltitudeM;
  private String status;
  private Integer favoriteCount;
  private Integer commentCount;
  private String tags;
  private LocalDateTime publishedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getCoverUrl() { return coverUrl; }
  public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public RouteDifficulty getDifficulty() { return difficulty; }
  public void setDifficulty(RouteDifficulty difficulty) { this.difficulty = difficulty; }
  public Double getDistanceKm() { return distanceKm; }
  public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
  public Integer getDurationMinutes() { return durationMinutes; }
  public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
  public Integer getAscentM() { return ascentM; }
  public void setAscentM(Integer ascentM) { this.ascentM = ascentM; }
  public Integer getMaxAltitudeM() { return maxAltitudeM; }
  public void setMaxAltitudeM(Integer maxAltitudeM) { this.maxAltitudeM = maxAltitudeM; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Integer getFavoriteCount() { return favoriteCount; }
  public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
  public Integer getCommentCount() { return commentCount; }
  public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
  public String getTags() { return tags; }
  public void setTags(String tags) { this.tags = tags; }
  public LocalDateTime getPublishedAt() { return publishedAt; }
  public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
