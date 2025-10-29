package be.school.portal.auth_service.common.exceptions;

import jakarta.annotation.Nonnull;

public interface CodedException {

  @Nonnull
  ErrorCode getErrorCode();
}
