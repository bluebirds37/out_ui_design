package com.trailnote.admin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AdminArchitectureConformanceTests {

  private static final Path ROOT = Path.of("src/main/java/com/trailnote/admin");

  @Test
  void interfaceControllersDoNotReachJdbcOrInlineSql() throws IOException {
    try (Stream<Path> stream = Files.walk(ROOT)) {
      List<Path> controllers = stream
          .filter(path -> path.toString().contains("/interfaces/http/"))
          .filter(path -> path.toString().endsWith(".java"))
          .toList();

      for (Path controller : controllers) {
        String content = Files.readString(controller);
        assertTrue(!content.contains("JdbcTemplate"), () -> controller + " should not use JdbcTemplate");
        assertTrue(!content.contains("NamedParameterJdbcTemplate"), () -> controller + " should not use NamedParameterJdbcTemplate");
        assertTrue(!content.contains("SELECT "), () -> controller + " should not embed SQL");
        assertTrue(!content.contains("UPDATE "), () -> controller + " should not embed SQL");
        assertTrue(!content.contains("INSERT "), () -> controller + " should not embed SQL");
        assertTrue(!content.contains("DELETE "), () -> controller + " should not embed SQL");
      }
    }
  }

  @Test
  void oldAdminDomainPackageReferencesAreGone() throws IOException {
    try (Stream<Path> stream = Files.walk(ROOT)) {
      for (Path file : stream.filter(path -> path.toString().endsWith(".java")).toList()) {
        String content = Files.readString(file);
        assertTrue(!content.contains("package com.trailnote.admin.domain."), () -> file + " still uses old package declaration");
        assertTrue(!content.contains("import com.trailnote.admin.domain."), () -> file + " still imports old admin.domain packages");
      }
    }
  }

  @Test
  void applicationLayerDoesNotImportInfrastructurePersistence() throws IOException {
    try (Stream<Path> stream = Files.walk(ROOT)) {
      List<Path> applicationFiles = stream
          .filter(path -> path.toString().contains("/application/"))
          .filter(path -> path.toString().endsWith(".java"))
          .toList();

      for (Path file : applicationFiles) {
        String content = Files.readString(file);
        assertTrue(
            !content.contains(".infrastructure.persistence."),
            () -> file + " should depend on domain gateways or repositories instead of infrastructure persistence"
        );
      }
    }
  }
}
