package be.school.portal.auth_service.application.services;

import be.school.portal.auth_service.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;

public interface UserTokenCreationService {

  UserToken create(@Nonnull UserAccount userAccount);

  record UserToken(String accessToken, String refreshToken) {}
}
