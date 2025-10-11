package be.school.portal.auth_service.common.exceptions;

import jakarta.annotation.Nonnull;
import java.io.Serial;

public class UserNotFoundException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = 2353021672963338528L;

  private static final String DEFAULT_MESSAGE = "User with username %s not found.";

  private UserNotFoundException(String message) {
    super(message);
  }

  public static UserNotFoundException ofUsername(@Nonnull String username) {
    return new UserNotFoundException(String.format(DEFAULT_MESSAGE, username));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.USER_NOT_FOUND;
  }
}
