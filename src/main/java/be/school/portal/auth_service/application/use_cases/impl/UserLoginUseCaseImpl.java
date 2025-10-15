package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.annotations.TrackLogin;
import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.application.services.UserTokenCreationService;
import be.school.portal.auth_service.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.common.exceptions.InvalidCredentialException;
import be.school.portal.auth_service.common.exceptions.UserInvalidStateException;
import be.school.portal.auth_service.common.exceptions.UserNotFoundException;
import be.school.portal.auth_service.common.utils.PasswordUtil;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation responsible for handling user login operations.
 *
 * <p>This service validates user credentials, checks account status, and generates a JWT token upon
 * successful authentication. The login process is executed asynchronously to improve responsiveness
 * and scalability when integrated with audit tracking or event-driven mechanisms.
 *
 * <p>Transactional behavior is configured with {@code readOnly = true} and {@code propagation =
 * REQUIRES_NEW}, ensuring the login process operates in a separate, non-mutating transaction
 * context.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserLoginUseCaseImpl implements UserLoginUseCase {

  private final UserRepository userRepository;
  private final UserTokenCreationService userTokenCreationService;
  private final LoginResponseMapper loginResponseMapper;

  public UserLoginUseCaseImpl(
      UserRepository userRepository,
      UserTokenCreationService userTokenCreationService,
      LoginResponseMapper loginResponseMapper) {
    this.userRepository = userRepository;
    this.userTokenCreationService = userTokenCreationService;
    this.loginResponseMapper = loginResponseMapper;
  }

  /**
   * Authenticates a user by validating the provided username and password.
   *
   * <p>Steps performed:
   *
   * <ol>
   *   <li>Fetches the user entity by username.
   *   <li>Checks if the user account is active.
   *   <li>Verifies the password against the stored hash.
   *   <li>Generates a JWT token for valid credentials.
   * </ol>
   *
   * <p>The method is annotated with {@link Async} and {@link TrackLogin}, enabling asynchronous
   * execution and audit tracking of login attempts.
   *
   * @param loginRequest the incoming request containing username and password
   * @return a {@link CompletableFuture} containing the {@link LoginResponse} with user and token
   *     information
   * @throws UserNotFoundException if no user exists for the given username
   * @throws UserInvalidStateException if the user account is inactive or locked
   * @throws InvalidCredentialException if the password verification fails
   */
  @Override
  @Async
  @TrackLogin
  public CompletableFuture<LoginResponse> login(@Nonnull LoginRequest loginRequest) {

    // Fetch the user
    final var existingUser =
        userRepository
            .findByUsername(loginRequest.username())
            .orElseThrow(() -> InvalidCredentialException.ofUsername(loginRequest.username()));

    // Check if user is active
    if (!existingUser.isActive()) {
      throw UserInvalidStateException.ofUsernameAndStatus(
          existingUser.getUsername(), existingUser.getStatus());
    }

    boolean passwordMatched =
        PasswordUtil.verify(loginRequest.password(), existingUser.getPassword());

    // Verify password
    if (!passwordMatched) {
      throw InvalidCredentialException.ofUsername(loginRequest.username());
    }

    // Generate JWT token
    final UserTokenCreationService.UserToken token = userTokenCreationService.create(existingUser);

    // Return response wrapped in CompletableFuture
    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(existingUser, token));
  }
}
