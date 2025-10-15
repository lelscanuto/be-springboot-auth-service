package be.school.portal.auth_service.application.services;

import be.school.portal.auth_service.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;

public interface UserTokenRevokeService {
  void revoke(@Nonnull UserAccount existingUser, @Nonnull String jti);
}
