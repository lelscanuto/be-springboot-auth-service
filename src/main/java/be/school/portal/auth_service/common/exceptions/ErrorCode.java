package be.school.portal.auth_service.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
  USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
  USER_FORBIDDEN(HttpStatus.FORBIDDEN),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND),
  USER_INVALID_CREDENTIAL(HttpStatus.UNAUTHORIZED),
  USER_INVALID_STATE(HttpStatus.UNPROCESSABLE_ENTITY);

  private final HttpStatus httpStatus;

  ErrorCode(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getCode() {
    return this.name();
  }
}
