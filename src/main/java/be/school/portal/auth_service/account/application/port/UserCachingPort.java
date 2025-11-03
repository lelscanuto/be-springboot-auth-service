package be.school.portal.auth_service.account.application.port;

import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.domain.projections.UserProjection;
import jakarta.annotation.Nonnull;

public interface UserCachingPort {

  UserProjection findByUsername(@Nonnull String username);

  void evictUserCache(@Nonnull String username);

  UserProjection updateCache(@Nonnull UserAccount userAccount);
}
