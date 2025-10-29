package be.school.portal.auth_service.role.domain.exceptions;

import be.school.portal.auth_service.account.common.exceptions.CodedException;
import be.school.portal.auth_service.account.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class RolePermissionNotExistException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = 6634546300611109557L;
  private static final String DEFAULT_MESSAGE =
      "Role with id %d does not have permission with id %d";

  private RolePermissionNotExistException(String message) {
    super(message);
  }

  public static RolePermissionNotExistException ofRoleIdAndPermissionId(
      @Nonnull Long roleId, @Nonnull Long permissionId) {
    return new RolePermissionNotExistException(
        String.format(DEFAULT_MESSAGE, roleId, permissionId));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.ROLE_PERMISSION_NOT_EXISTS;
  }
}
