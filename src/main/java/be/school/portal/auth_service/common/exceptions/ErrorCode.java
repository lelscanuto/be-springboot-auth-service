package be.school.portal.auth_service.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  TEST(HttpStatus.INTERNAL_SERVER_ERROR);

  private final HttpStatus httpStatus;

  ErrorCode(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getCode() {
    return this.name();
  }
}
