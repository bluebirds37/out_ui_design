package com.trailnote.api.route.domain.model;

import java.util.LinkedHashSet;
import java.util.List;

public record RouteTagSet(List<String> values) {

  public static RouteTagSet fromList(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return new RouteTagSet(List.of());
    }
    LinkedHashSet<String> normalized = new LinkedHashSet<>();
    tags.stream()
        .map(tag -> tag == null ? "" : tag.trim())
        .filter(tag -> !tag.isEmpty())
        .forEach(normalized::add);
    return new RouteTagSet(List.copyOf(normalized));
  }

  public static RouteTagSet fromRaw(String rawTags) {
    if (rawTags == null || rawTags.isBlank()) {
      return new RouteTagSet(List.of());
    }
    return fromList(List.of(rawTags.split(",")));
  }

  public String toRaw() {
    if (values.isEmpty()) {
      return null;
    }
    return String.join(",", values);
  }
}
