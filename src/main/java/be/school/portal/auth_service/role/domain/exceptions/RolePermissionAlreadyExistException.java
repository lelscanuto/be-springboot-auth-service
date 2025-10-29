package be.school.portal.auth_service.role.domain.exceptions;

import be.school.portal.auth_service.account.common.exceptions.CodedException;
import be.school.portal.auth_service.account.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class RolePermissionAlreadyExistException extends RuntimeException
    implements CodedException {

  @Serial private static final long serialVersionUID = -7838568001955789338L;
  private static final String DEFAULT_MESSAGE = "Role with id %d already has permission with id %d";

  private RolePermissionAlreadyExistException(String message) {
    super(message);
  }

  public static RolePermissionAlreadyExistException ofRoleIdAndPermissionId(
      @Nonnull Long roleId, @Nonnull Long permissionId) {
    return new RolePermissionAlreadyExistException(
        String.format(DEFAULT_MESSAGE, roleId, permissionId));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.ROLE_PERMISSION_CONFLICT;
  }
}
