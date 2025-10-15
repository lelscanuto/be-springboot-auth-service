package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.application.services.UserTokenRenewalService;
import be.school.portal.auth_service.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.common.component.JwtTokenComponent;
import be.school.portal.auth_service.common.exceptions.InvalidCredentialException;
import be.school.portal.auth_service.common.exceptions.UserInvalidStateException;
import be.school.portal.auth_service.common.exceptions.UserNotFoundException;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Use case implementation for refreshing user tokens.
 *
 * <p>This class validates the provided refresh token, ensures it is a refresh token, retrieves the
 * user, checks the user's state, and issues new tokens.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Validated
public class UserTokenRefreshUseCaseImpl implements UserTokenRefreshUseCase {

  private final UserRepository userRepository;
  private final UserTokenRenewalService userTokenRenewalService;
  private final LoginResponseMapper loginResponseMapper;
  private final JwtTokenComponent jwtTokenComponent;

  /**
   * Constructs a new UserTokenRefreshUseCaseImpl.
   *
   * @param userRepository the repository for user data access
   * @param userTokenRenewalService the service for renewing user tokens
   * @param loginResponseMapper the mapper for creating login responses
   * @param jwtTokenComponent the component for JWT token operations
   */
  public UserTokenRefreshUseCaseImpl(
      UserRepository userRepository,
      UserTokenRenewalService userTokenRenewalService,
      LoginResponseMapper loginResponseMapper,
      JwtTokenComponent jwtTokenComponent) {
    this.userRepository = userRepository;
    this.userTokenRenewalService = userTokenRenewalService;
    this.loginResponseMapper = loginResponseMapper;
    this.jwtTokenComponent = jwtTokenComponent;
  }

  /**
   * Refreshes the user's tokens using the provided refresh token.
   *
   * <ul>
   *   <li>Validates the refresh token
   *   <li>Ensures the token is a refresh token
   *   <li>Retrieves the user and checks if active
   *   <li>Renews tokens and returns a new login response
   * </ul>
   *
   * @param tokenRequest the request containing the refresh token
   * @return a {@link CompletableFuture} with the new {@link LoginResponse}
   * @throws InvalidCredentialException if the token is invalid or not a refresh token
   * @throws UserNotFoundException if the user does not exist
   */
  @Override
  @Async
  public CompletableFuture<LoginResponse> refresh(@Valid @Nonnull TokenRequest tokenRequest) {

    // Extract the refresh token from the request
    final var refreshToken = tokenRequest.token();

    // 1️⃣ Validate refresh token
    if (!jwtTokenComponent.validateToken(refreshToken)) {
      throw InvalidCredentialException.ofToken(refreshToken);
    }

    // Ensure it’s a refresh token (not access)
    JwtTokenComponent.TokenType tokenType = jwtTokenComponent.getTokenType(refreshToken);
    if (JwtTokenComponent.TokenType.REFRESH != tokenType) {
      throw InvalidCredentialException.ofTokenType(tokenType.name());
    }

    // Extract username and find the user
    final var username = jwtTokenComponent.getUsernameFromToken(refreshToken);
    final var jti = jwtTokenComponent.getJtiFromToken(refreshToken);

    // Retrieve the existing user or throw an exception if not found
    final var existingUser =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> InvalidCredentialException.ofUsername(username));

    // Check if user is active
    if (!existingUser.isActive()) {
      throw UserInvalidStateException.ofUsernameAndStatus(
          existingUser.getUsername(), existingUser.getStatus());
    }

    final var userToken = userTokenRenewalService.renewTokens(existingUser, jti);

    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(existingUser, userToken));
  }
}
