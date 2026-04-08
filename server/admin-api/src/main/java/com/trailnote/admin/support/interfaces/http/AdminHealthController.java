package com.trailnote.admin.support.interfaces.http;

import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.health.ServiceStatus;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/public")
public class AdminHealthController {

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${app.environment:local}")
  private String environment;

  @Value("${app.version:0.0.1-SNAPSHOT}")
  private String version;

  @GetMapping("/health")
  public ApiResponse<ServiceStatus> health() {
    return ApiResponse.success(
        new ServiceStatus(applicationName, environment, version, OffsetDateTime.now())
    );
  }
}
