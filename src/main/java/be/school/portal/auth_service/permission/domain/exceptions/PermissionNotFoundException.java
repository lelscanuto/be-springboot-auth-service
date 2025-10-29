package be.school.portal.auth_service.permission.domain.exceptions;

import be.school.portal.auth_service.account.common.exceptions.CodedException;
import be.school.portal.auth_service.account.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class PermissionNotFoundException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = -1035562500659165628L;
  private static final String DEFAULT_MESSAGE = "Permission not found: %d";

  public PermissionNotFoundException(String message) {
    super(message);
  }

  public static PermissionNotFoundException ofId(@Nonnull Long permissionId) {
    return new PermissionNotFoundException(String.format(DEFAULT_MESSAGE, permissionId));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PERMISSION_NOT_FOUND;
  }
}
