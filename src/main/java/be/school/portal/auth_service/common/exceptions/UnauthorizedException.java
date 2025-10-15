package be.school.portal.auth_service.common.exceptions;

import jakarta.annotation.Nonnull;
import java.io.Serial;

/**
 * Exception thrown when a user attempts to perform an action without proper authentication or
 * authorization.
 *
 * <p>Used to represent 401 Unauthorized errors in the authentication and authorization layers.
 */
public class UnauthorizedException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = 1422347096032212152L;

  // 🔒 Common message constants
  private static final String MSG_USER_MISMATCH =
      "Authenticated user does not match the token owner: ";
  private static final String MSG_MISSING_AUTH =
      "User not authenticated or security context is missing";

  private UnauthorizedException(String message) {
    super(message);
  }

  /**
   * Creates an exception indicating that the currently authenticated user does not match the token
   * owner.
   *
   * @param username the username extracted from the token
   * @return a new {@link UnauthorizedException}
   */
  public static UnauthorizedException ofUserMismatch(String username) {
    return new UnauthorizedException(MSG_USER_MISMATCH + username);
  }

  /**
   * Creates an exception indicating that no user is authenticated in the security context.
   *
   * @return a new {@link UnauthorizedException}
   */
  public static UnauthorizedException ofMissingAuth() {
    return new UnauthorizedException(MSG_MISSING_AUTH);
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.USER_UNAUTHORIZED;
  }
}
