package com.trailnote.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.trailnote")
public class AdminApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminApiApplication.class, args);
  }
}
