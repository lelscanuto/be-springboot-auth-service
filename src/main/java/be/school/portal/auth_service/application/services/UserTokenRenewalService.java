package be.school.portal.auth_service.application.services;

import be.school.portal.auth_service.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;

public interface UserTokenRenewalService {
  UserTokenCreationService.UserToken renewTokens(
      @Nonnull UserAccount existingUser, @Nonnull String jti);
}
