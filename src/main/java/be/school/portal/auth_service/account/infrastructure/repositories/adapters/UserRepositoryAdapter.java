package be.school.portal.auth_service.account.infrastructure.repositories.adapters;

import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.infrastructure.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

  private final UserRepository userRepository;

  public UserRepositoryAdapter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<UserAccount> findByUsername(@Nonnull String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public UserAccount save(@Nonnull UserAccount userAccount) {
    return userRepository.save(userAccount);
  }

  @Override
  public UserAccount saveAndFlush(@Nonnull UserAccount userAccount) {
    return userRepository.saveAndFlush(userAccount);
  }

  @Override
  public boolean existsByRolesName(String roleName) {
    return userRepository.existsByRolesName(roleName);
  }
}
