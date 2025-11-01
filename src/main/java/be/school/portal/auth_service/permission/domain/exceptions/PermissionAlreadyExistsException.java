package be.school.portal.auth_service.permission.domain.exceptions;

import be.school.portal.auth_service.common.exceptions.CodedException;
import be.school.portal.auth_service.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class PermissionAlreadyExistsException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = -9108192158208762465L;
  private static final String DEFAULT_MESSAGE = "Permission already exists: %s";

  private PermissionAlreadyExistsException(String message) {
    super(message);
  }

  public static PermissionAlreadyExistsException ofName(String permissionName) {
    return new PermissionAlreadyExistsException(String.format(DEFAULT_MESSAGE, permissionName));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PERMISSION_ALREADY_EXISTS;
  }
}
