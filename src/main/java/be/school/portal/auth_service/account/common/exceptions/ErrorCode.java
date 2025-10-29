package be.school.portal.auth_service.account.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND),
  ROLE_NOT_FOUND(HttpStatus.NOT_FOUND),
  ROLE_CONFLICT(HttpStatus.CONFLICT),
  ROLE_PERMISSION_CONFLICT(HttpStatus.CONFLICT),
  ROLE_PERMISSION_NOT_EXISTS(HttpStatus.NOT_FOUND),
  ROLE_STILL_ASSIGNED_TO_ACTIVE_USER(HttpStatus.UNPROCESSABLE_ENTITY);

  private final HttpStatus httpStatus;

  ErrorCode(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getCode() {
    return this.name();
  }
}
