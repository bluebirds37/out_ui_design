package com.trailnote.admin.auth.support;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebMvcConfigurer implements WebMvcConfigurer {

  private final AdminTokenInterceptor adminTokenInterceptor;

  public AdminWebMvcConfigurer(AdminTokenInterceptor adminTokenInterceptor) {
    this.adminTokenInterceptor = adminTokenInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminTokenInterceptor)
        .addPathPatterns("/admin/**")
        .excludePathPatterns(
            "/admin/public/**",
            "/admin/auth/login"
        );
  }
}
