package be.school.portal.auth_service.role.domain.exceptions;

import be.school.portal.auth_service.common.exceptions.CodedException;
import be.school.portal.auth_service.common.exceptions.ErrorCode;
import jakarta.annotation.Nonnull;
import java.io.Serial;

public class RoleAlreadyExistsException extends RuntimeException implements CodedException {

  @Serial private static final long serialVersionUID = -2524406996215630994L;
  private static final String DEFAULT_MESSAGE = "Role with name %s already exists.";

  private RoleAlreadyExistsException(String message) {
    super(message);
  }

  public static RoleAlreadyExistsException ofName(@Nonnull String name) {
    return new RoleAlreadyExistsException(String.format(DEFAULT_MESSAGE, name));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.ROLE_CONFLICT;
  }
}
