package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.annotations.TrackLogin;
import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.application.providers.JwtAuthenticationProvider;
import be.school.portal.auth_service.application.services.UserTokenCreationService;
import be.school.portal.auth_service.application.use_cases.UserLoginUseCase;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

  private final AuthenticationManager authenticationManager;
  private final UserTokenCreationService userTokenCreationService;
  private final LoginResponseMapper loginResponseMapper;

  public UserLoginUseCaseImpl(
      AuthenticationManager authenticationManager,
      UserTokenCreationService userTokenCreationService,
      LoginResponseMapper loginResponseMapper) {
    this.authenticationManager = authenticationManager;

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
   * @throws org.springframework.security.authentication.BadCredentialsException if no user exists
   *     for the given username
   */
  @Override
  @Async
  @TrackLogin
  public CompletableFuture<LoginResponse> login(@Nonnull LoginRequest loginRequest) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password() // raw password
                ));

    JwtAuthenticationProvider.UserPrincipalContext userPrincipalContext =
        (JwtAuthenticationProvider.UserPrincipalContext) authentication.getPrincipal();

    // Generate JWT token
    final UserTokenCreationService.UserToken token =
        userTokenCreationService.create(userPrincipalContext.getContext());

    // Return response wrapped in CompletableFuture
    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(userPrincipalContext.getContext(), token));
  }
}
