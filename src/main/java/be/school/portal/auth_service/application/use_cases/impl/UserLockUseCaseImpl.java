package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.mappers.UserProjectionMapper;
import be.school.portal.auth_service.application.use_cases.UserLockUseCase;
import be.school.portal.auth_service.common.exceptions.UserNotFoundException;
import be.school.portal.auth_service.domain.enums.UserStatus;
import be.school.portal.auth_service.domain.projections.UserProjection;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation responsible for locking user accounts.
 *
 * <p>This class is part of the application domain layer and provides functionality to mark a user
 * account as {@code LOCKED}. Locking may occur as a result of multiple failed login attempts,
 * administrative action, or other business rules.
 *
 * <p>Execution of this use case is asynchronous to avoid blocking the main workflow (for example,
 * login request handling).
 *
 * <p>The method is transactional to ensure atomic updates to the user's account state.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserLockUseCaseImpl implements UserLockUseCase {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLockUseCaseImpl.class);

  private final UserRepository userRepository;
  private final UserProjectionMapper userProjectionMapper;

  /**
   * Constructs the use case with required dependencies.
   *
   * @param userRepository the repository for accessing and persisting user entities
   * @param userProjectionMapper the mapper for converting user entities to lightweight DTOs
   */
  public UserLockUseCaseImpl(
      UserRepository userRepository, UserProjectionMapper userProjectionMapper) {
    this.userRepository = userRepository;
    this.userProjectionMapper = userProjectionMapper;
  }

  /**
   * Locks a user account by setting its {@link UserStatus} to {@code LOCKED}.
   *
   * <p>If the specified user cannot be found, a {@link UserNotFoundException} is thrown. This
   * method executes asynchronously in a separate thread to prevent blocking the caller.
   *
   * @param username the username of the account to lock
   * @return a {@link CompletableFuture} containing a {@link UserProjection} representation of the
   *     locked user
   * @throws UserNotFoundException if no user with the given username exists
   */
  @Override
  @Async
  public CompletableFuture<UserProjection> lockUser(String username) {

    LOGGER.debug("Locking user with username: {}", username);

    // Retrieve the existing user or throw an exception if not found
    final var existingUser =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> UserNotFoundException.ofUsername(username));

    // Update the user's status to LOCKED
    existingUser.setStatus(UserStatus.LOCKED);

    // Persist the changes to the database
    userRepository.save(existingUser);

    LOGGER.debug("Successfully locked user with username: {}", username);

    return CompletableFuture.completedFuture(userProjectionMapper.toUserLiteDTO(existingUser));
  }
}
