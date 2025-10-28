package be.school.portal.auth_service.account.common.exceptions;

import jakarta.annotation.Nonnull;

public interface CodedException {

  @Nonnull
  ErrorCode getErrorCode();
}
