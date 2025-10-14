package be.school.portal.auth_service.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("auth-user-err-01", HttpStatus.NOT_FOUND),
  USER_INVALID_CREDENTIAL("auth-user-err-02", HttpStatus.UNAUTHORIZED),
  USER_INVALID_STATE("auth-user-err-03", HttpStatus.UNPROCESSABLE_ENTITY);

  private final String code;
  private final HttpStatus httpStatus;

  ErrorCode(String code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }
}
