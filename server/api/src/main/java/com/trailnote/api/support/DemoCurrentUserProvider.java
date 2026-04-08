package com.trailnote.api.support;

import org.springframework.stereotype.Component;

@Component
public class DemoCurrentUserProvider {

  public Long getCurrentUserId() {
    return 1001L;
  }
}
