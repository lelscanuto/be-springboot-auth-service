package be.school.portal.auth_service.permission.domain.exceptions;

import be.school.portal.auth_service.common.exceptions.CodedException;
import be.school.portal.auth_service.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class PermissionUsedInRoleException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = -4707432612103437389L;

  private static final String DEFAULT_MESSAGE =
      "Permission '%s' is still assigned to active roles.";

  private PermissionUsedInRoleException(String message) {
    super(message);
  }

  public static PermissionUsedInRoleException ofName(String name) {
    return new PermissionUsedInRoleException(String.format(DEFAULT_MESSAGE, name));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PERMISSION_STILL_ASSIGNED_TO_ACTIVE_ROLE;
  }
}
