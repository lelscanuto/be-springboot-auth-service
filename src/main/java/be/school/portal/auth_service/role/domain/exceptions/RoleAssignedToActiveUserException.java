package be.school.portal.auth_service.role.domain.exceptions;

import be.school.portal.auth_service.account.common.exceptions.CodedException;
import be.school.portal.auth_service.account.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class RoleAssignedToActiveUserException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = 9143802937409452118L;
  private static final String DEFAULT_MESSAGE = "Role '%s' is still assigned to active users.";

  private RoleAssignedToActiveUserException(String message) {
    super(message);
  }

  public static RoleAssignedToActiveUserException ofName(String name) {
    return new RoleAssignedToActiveUserException(String.format(DEFAULT_MESSAGE, name));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.ROLE_STILL_ASSIGNED_TO_ACTIVE_USER;
  }
}
