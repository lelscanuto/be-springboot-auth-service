package be.school.portal.auth_service.account.infrastructure.repositories.adapters;

import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.infrastructure.repositories.UserRepository;
import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.logging.LoggerName;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Trace(logger = LoggerName.REPOSITORY_LOGGER)
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class UserRepositoryJpaAdapter implements UserRepositoryPort {

  private final UserRepository userRepository;

  public UserRepositoryJpaAdapter(UserRepository userRepository) {
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
