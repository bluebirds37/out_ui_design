package com.trailnote.common.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(error -> error.getField() + " " + error.getDefaultMessage())
        .orElse("request validation failed");
    return ApiResponse.failure(ErrorCode.BAD_REQUEST, message);
  }

  @ExceptionHandler({
      ConstraintViolationException.class,
      HttpMessageNotReadableException.class,
      IllegalArgumentException.class
  })
  public ApiResponse<Void> handleBadRequest(Exception exception) {
    return ApiResponse.failure(ErrorCode.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ApiResponse<Void> handleUnauthorized(UnauthorizedException exception) {
    return ApiResponse.failure(ErrorCode.UNAUTHORIZED, exception.getMessage());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ApiResponse<Void> handleForbidden(ForbiddenException exception) {
    return ApiResponse.failure(ErrorCode.FORBIDDEN, exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleUnexpected(Exception exception) {
    return ApiResponse.failure(ErrorCode.INTERNAL_ERROR, exception.getMessage());
  }
}
