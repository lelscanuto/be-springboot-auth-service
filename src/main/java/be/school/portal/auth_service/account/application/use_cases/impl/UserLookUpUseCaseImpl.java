package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class UserLookUpUseCaseImpl implements UserLookUpUseCase {

  private final UserRepositoryPort userRepositoryPort;

  public UserLookUpUseCaseImpl(UserRepositoryPort userRepositoryPort) {
    this.userRepositoryPort = userRepositoryPort;
  }

  @Override
  public boolean existsByRole(@Nonnull String name) {
    return userRepositoryPort.existsByRolesName(name);
  }
}
