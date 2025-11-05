package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.domain.exception.UserNotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application-layer implementation of {@link UserLookUpUseCase} responsible for retrieving user
 * account information from the persistence layer.
 *
 * <p>This use case serves as an intermediary between the domain and infrastructure layers,
 * delegating data access operations to {@link UserRepositoryPort} while ensuring domain-level
 * integrity and consistency.
 *
 * <p><strong>Key Responsibilities:</strong>
 *
 * <ul>
 *   <li>Determine the existence of users associated with a given role name.
 *   <li>Retrieve user entities based on username, enforcing domain rules and validation.
 * </ul>
 *
 * <p>The use case is marked as {@code readOnly = true} to prevent unintended state modifications
 * and participates in an existing transaction context using {@link Propagation#REQUIRED}.
 *
 * @see UserRepositoryPort
 * @see UserAccount
 * @see UserNotFoundException
 * @author Francis Jorell J. Canuto
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class UserLookUpUseCaseImpl implements UserLookUpUseCase {

  private final UserRepositoryPort userRepositoryPort;

  /**
   * Constructs a new {@code UserLookUpUseCaseImpl} with the specified repository port dependency.
   *
   * @param userRepositoryPort the repository abstraction responsible for accessing user data
   */
  public UserLookUpUseCaseImpl(UserRepositoryPort userRepositoryPort) {
    this.userRepositoryPort = userRepositoryPort;
  }

  /**
   * Determines whether any user exists with the specified role name.
   *
   * <p>This method queries the persistence layer to check for the presence of users associated with
   * the given role. It is typically used for administrative validation or role cleanup operations.
   *
   * @param name the role name to check for associated users; must not be {@code null}
   * @return {@code true} if one or more users are assigned the specified role; {@code false}
   *     otherwise
   */
  @Override
  public boolean existsByRole(@Nonnull String name) {
    return userRepositoryPort.existsByRolesName(name);
  }

  /**
   * Retrieves a {@link UserAccount} entity associated with the given username.
   *
   * <p>If no matching user exists, a {@link UserNotFoundException} is thrown to signal a
   * domain-level violation. The lookup is case-sensitive unless otherwise handled by the repository
   * implementation.
   *
   * @param username the username to search for; must not be {@code null}
   * @return the corresponding {@link UserAccount} entity
   * @throws UserNotFoundException if no user with the specified username exists
   */
  @Override
  public UserAccount findByUsername(@Nonnull String username) {
    return userRepositoryPort
        .findByUsername(username)
        .orElseThrow(() -> UserNotFoundException.ofUserName(username));
  }
}
