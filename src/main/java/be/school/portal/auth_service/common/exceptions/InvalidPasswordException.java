package be.school.portal.auth_service.common.exceptions;

import jakarta.annotation.Nonnull;
import java.io.Serial;

public class InvalidPasswordException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = -6371429883775842405L;

  private static final String DEFAULT_MESSAGE = "User [%s] provided an invalid password.";

  private InvalidPasswordException(@Nonnull String message) {
    super(message);
  }

  public static InvalidPasswordException ofUsername(@Nonnull String username) {
    return new InvalidPasswordException(String.format(username, DEFAULT_MESSAGE));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.INVALID_PASSWORD;
  }
}
