package be.school.portal.auth_service.account.application.port;

import be.school.portal.auth_service.account.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;
import java.util.Optional;

public interface UserRepositoryPort {
  Optional<UserAccount> findByUsername(@Nonnull String username);

  UserAccount save(@Nonnull UserAccount userAccount);

  UserAccount saveAndFlush(@Nonnull UserAccount userAccount);
}
