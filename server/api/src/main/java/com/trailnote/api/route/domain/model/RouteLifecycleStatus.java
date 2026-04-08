package com.trailnote.api.route.domain.model;

public enum RouteLifecycleStatus {
  DRAFT,
  PENDING_REVIEW,
  PUBLISHED,
  REJECTED,
  ARCHIVED;

  public String value() {
    return name();
  }

  public static RouteLifecycleStatus fromValue(String value) {
    return value == null ? null : RouteLifecycleStatus.valueOf(value);
  }
}
