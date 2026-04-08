package com.trailnote.common.api;

import java.util.List;

public record PageResponse<T>(
    List<T> records,
    long total,
    long page,
    long pageSize
) {
  public static <T> PageResponse<T> of(List<T> records, long total, long page, long pageSize) {
    return new PageResponse<>(records, total, page, pageSize);
  }
}
