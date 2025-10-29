package be.school.portal.auth_service.role.domain.exceptions;

import be.school.portal.auth_service.common.exceptions.CodedException;
import be.school.portal.auth_service.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class RoleNotFoundException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = 1167275506096531944L;
  private static final String DEFAULT_MESSAGE = "Role not found: %d";

  private RoleNotFoundException(String message) {
    super(message);
  }

  public static RoleNotFoundException ofId(@Nonnull Long roleId) {
    return new RoleNotFoundException(String.format(DEFAULT_MESSAGE, roleId));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.ROLE_NOT_FOUND;
  }
}
