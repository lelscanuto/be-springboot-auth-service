package be.school.portal.auth_service.account.application.internal.services;

import be.school.portal.auth_service.account.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;

public interface UserTokenRenewalService {
  UserTokenCreationService.UserToken renewTokens(
      @Nonnull UserAccount existingUser, @Nonnull String jti);
}
