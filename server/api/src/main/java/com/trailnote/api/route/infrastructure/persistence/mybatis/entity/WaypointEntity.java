package com.trailnote.api.route.infrastructure.persistence.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.trailnote.api.route.domain.model.WaypointType;
import java.time.LocalDateTime;

@TableName("tn_waypoint")
public class WaypointEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long routeId;
  private String title;
  private WaypointType waypointType;
  private String description;
  private Double latitude;
  private Double longitude;
  private Integer altitudeM;
  private Integer sortOrder;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getRouteId() { return routeId; }
  public void setRouteId(Long routeId) { this.routeId = routeId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public WaypointType getWaypointType() { return waypointType; }
  public void setWaypointType(WaypointType waypointType) { this.waypointType = waypointType; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Double getLatitude() { return latitude; }
  public void setLatitude(Double latitude) { this.latitude = latitude; }
  public Double getLongitude() { return longitude; }
  public void setLongitude(Double longitude) { this.longitude = longitude; }
  public Integer getAltitudeM() { return altitudeM; }
  public void setAltitudeM(Integer altitudeM) { this.altitudeM = altitudeM; }
  public Integer getSortOrder() { return sortOrder; }
  public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
