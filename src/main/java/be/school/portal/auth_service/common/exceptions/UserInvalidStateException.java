package be.school.portal.auth_service.common.exceptions;

import be.school.portal.auth_service.domain.enums.UserStatus;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class UserInvalidStateException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = -377932266630370938L;

  private static final String DEFAULT_MESSAGE = "User [%s] is [%s].";

  private UserInvalidStateException(String message) {
    super(message);
  }

  public static UserInvalidStateException ofUsernameAndStatus(
      String username, UserStatus userStatus) {
    return new UserInvalidStateException(String.format(DEFAULT_MESSAGE, username, userStatus));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.USER_INVALID_STATE;
  }
}
