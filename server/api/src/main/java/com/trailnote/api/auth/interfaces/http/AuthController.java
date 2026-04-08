package com.trailnote.api.auth.interfaces.http;

import com.trailnote.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    String nickname = request.account().contains("@") ? "TrailNote User" : request.account();
    return ApiResponse.success(new LoginResponse("mock-token-for-bootstrap", nickname));
  }

  public record LoginRequest(
      @NotBlank(message = "must not be blank")
      String account,
      @NotBlank(message = "must not be blank")
      @Size(min = 6, message = "must be at least 6 characters")
      String password
  ) {
  }

  public record LoginResponse(String accessToken, String nickname) {
  }
}
