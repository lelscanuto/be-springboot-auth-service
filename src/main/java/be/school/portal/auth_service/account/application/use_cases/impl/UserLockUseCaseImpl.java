package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.account.application.port.UserCachingPort;
import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.application.use_cases.UserLockUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.domain.enums.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service (use case) responsible for enforcing user account locking policies.
 *
 * <p>This class is part of the <strong>application layer</strong> and encapsulates the business
 * logic required to mark a user account as {@link
 * be.school.portal.auth_service.account.domain.enums.UserStatus#LOCKED}. Account locking may occur
 * as a result of:
 *
 * <ul>
 *   <li>Exceeding the maximum number of failed login attempts
 *   <li>Administrative security enforcement
 *   <li>Policy-driven rules or automated fraud detection workflows
 * </ul>
 *
 * <p>The use case operates in its own transactional context ({@code REQUIRES_NEW}) to ensure that
 * the locking operation is atomic and independent of the caller’s transaction. This guarantees that
 * a user lock persists even if the initiating transaction is rolled back.
 *
 * <p>Execution is intended to be asynchronous (usually triggered via an application event) to
 * prevent blocking user-facing operations such as login attempts.
 *
 * <p>This implementation interacts with:
 *
 * <ul>
 *   <li>{@link be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase} for
 *       retrieving user data
 *   <li>{@link be.school.portal.auth_service.account.application.port.UserRepositoryPort} for
 *       persistence operations
 *   <li>{@link be.school.portal.auth_service.account.application.port.UserCachingPort} for cache
 *       synchronization
 * </ul>
 *
 * <h3>Thread Safety</h3>
 *
 * <p>This service is thread-safe due to its stateless design and reliance on Spring-managed
 * transactional boundaries.
 *
 * <h3>Example Usage</h3>
 *
 * <pre>{@code
 * userLockUseCase.lockUser("john.doe");
 * }</pre>
 *
 * @author Francis Jorell J. Canuto
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserLockUseCaseImpl implements UserLockUseCase {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLockUseCaseImpl.class);

  private final UserLookUpUseCase userLookUpUseCase;
  private final UserRepositoryPort userRepositoryPort;
  private final UserCachingPort userCachingPort;

  /**
   * Constructs a new instance of {@link UserLockUseCaseImpl} with the required dependencies.
   *
   * @param userLookUpUseCase the use case responsible for retrieving user details from persistent
   *     storage
   * @param userRepositoryPort the repository port abstraction for persisting user entities
   * @param userCachingPort the port responsible for maintaining user cache consistency
   */
  public UserLockUseCaseImpl(
      UserLookUpUseCase userLookUpUseCase,
      UserRepositoryPort userRepositoryPort,
      UserCachingPort userCachingPort) {
    this.userLookUpUseCase = userLookUpUseCase;
    this.userRepositoryPort = userRepositoryPort;
    this.userCachingPort = userCachingPort;
  }

  /**
   * Locks the specified user account by updating its status to {@link
   * be.school.portal.auth_service.account.domain.enums.UserStatus#LOCKED}.
   *
   * <p>This method ensures that the operation is:
   *
   * <ul>
   *   <li><strong>Atomic</strong> — performed in its own transaction to avoid partial updates
   *   <li><strong>Idempotent</strong> — repeated invocations on an already locked user will result
   *       in the same state
   *   <li><strong>Consistent</strong> — ensures that both persistent and cached representations
   *       reflect the locked state
   * </ul>
   *
   * @param username the unique username of the account to be locked
   * @return the updated {@link be.school.portal.auth_service.account.domain.entities.UserAccount}
   *     instance representing the locked user
   * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the
   *     specified user cannot be found
   */
  @Override
  public UserAccount lockUser(String username) {
    LOGGER.debug("Initiating lock operation for user: {}", username);

    // Retrieve the existing user or throw an exception if not found
    final var existingUser = userLookUpUseCase.findByUsername(username);

    // Update the user's status to LOCKED
    existingUser.setStatus(UserStatus.LOCKED);

    // Persist the updated user state
    userRepositoryPort.save(existingUser);

    // Update the user cache to ensure consistency
    userCachingPort.updateCache(existingUser);

    LOGGER.debug("Successfully locked user account: {}", username);

    return existingUser;
  }
}
