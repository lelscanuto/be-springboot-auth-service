package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.application.services.UserTokenRenewalService;
import be.school.portal.auth_service.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.common.component.RefreshTokenProcessor;
import be.school.portal.auth_service.domain.enums.UserStatus;
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
  private final RefreshTokenProcessor refreshTokenProcessor;

  /**
   * Constructs a new UserTokenRefreshUseCaseImpl.
   *
   * @param userRepository the repository for user data access
   * @param userTokenRenewalService the service for renewing user tokens
   * @param loginResponseMapper the mapper for creating login responses
   * @param refreshTokenProcessor the component for JWT token operations
   */
  public UserTokenRefreshUseCaseImpl(
      UserRepository userRepository,
      UserTokenRenewalService userTokenRenewalService,
      LoginResponseMapper loginResponseMapper,
      RefreshTokenProcessor refreshTokenProcessor) {
    this.userRepository = userRepository;
    this.userTokenRenewalService = userTokenRenewalService;
    this.loginResponseMapper = loginResponseMapper;
    this.refreshTokenProcessor = refreshTokenProcessor;
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
   * @throws org.springframework.security.authentication.BadCredentialsException if the token is
   *     invalid or not a refresh token
   * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the user
   *     does not exists
   * @throws org.springframework.security.authentication.LockedException if the user is locked
   * @throws org.springframework.security.authentication.DisabledException if the user is inactive
   */
  @Override
  @Async
  public CompletableFuture<LoginResponse> refresh(@Valid @Nonnull TokenRequest tokenRequest) {

    // Extract the refresh token from the request
    final var refreshToken = tokenRequest.token();

    // Process and validate the refresh token
    final RefreshTokenProcessor.RefreshTokenData refreshTokenData =
        refreshTokenProcessor.process(refreshToken);

    // Retrieve the existing user or throw an exception if not found
    final var existingUser =
        userRepository
            .findByUsername(refreshTokenData.username())
            .orElseThrow(
                () ->
                    SecurityExceptionFactory.UsernameNotFoundExceptionFactory.ofUsername(
                        refreshTokenData.username()));

    // Check if user is active
    if (UserStatus.INACTIVE == existingUser.getStatus()) {
      throw SecurityExceptionFactory.UserStateExceptionFactory.disabled(existingUser.getUsername());
    }

    // Check if user is active
    if (UserStatus.LOCKED == existingUser.getStatus()) {
      throw SecurityExceptionFactory.UserStateExceptionFactory.locked(existingUser.getUsername());
    }

    // Renew tokens for the user
    final var userToken = userTokenRenewalService.renewTokens(existingUser, refreshTokenData.jti());

    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(existingUser, userToken));
  }
}
