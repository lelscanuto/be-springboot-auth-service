package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.common.dto.TokenRequest;
import jakarta.annotation.Nonnull;

public interface UserTokenRevokeUseCase {
  void revoke(@Nonnull TokenRequest tokenRequest);
}
