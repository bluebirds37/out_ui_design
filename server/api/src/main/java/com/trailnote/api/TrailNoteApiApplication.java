package com.trailnote.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({
    "com.trailnote.api.route.infrastructure.persistence.mybatis.mapper",
    "com.trailnote.api.interaction.infrastructure.persistence.mybatis.mapper",
    "com.trailnote.api.social.infrastructure.persistence.mybatis.mapper",
    "com.trailnote.api.user.infrastructure.persistence.mybatis.mapper"
})
@SpringBootApplication(scanBasePackages = "com.trailnote")
public class TrailNoteApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(TrailNoteApiApplication.class, args);
  }
}
