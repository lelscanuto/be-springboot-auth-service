package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.account.application.dto.UserLoginDTO;
import be.school.portal.auth_service.account.application.internal.services.UserTokenRenewalService;
import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.account.domain.exception.UserNotFoundException;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.common.component.RefreshTokenProcessor;
import be.school.portal.auth_service.common.dto.TokenRequest;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Application-layer implementation of {@link UserTokenRefreshUseCase} responsible for securely
 * renewing user authentication tokens.
 *
 * <p>This use case validates the integrity and type of the provided refresh token, verifies user
 * status, and issues a new access/refresh token pair through the {@link UserTokenRenewalService}.
 * It ensures compliance with security rules defined in the domain layer.
 *
 * <p><strong>Transactional Behavior:</strong> Executes within a new transaction context ({@link
 * Propagation#REQUIRES_NEW}) to guarantee isolation from the calling workflow and to ensure atomic
 * token renewal.
 *
 * <p><strong>Responsibilities:</strong>
 *
 * <ul>
 *   <li>Validate the provided refresh token and extract its claims.
 *   <li>Retrieve the associated user entity and confirm its active status.
 *   <li>Delegate token regeneration to the renewal service.
 *   <li>Return a {@link UserLoginDTO} containing updated authentication details.
 * </ul>
 *
 * @see UserTokenRenewalService
 * @see RefreshTokenProcessor
 * @see be.school.portal.auth_service.account.domain.entities.UserAccount
 * @author Francis Jorell J. Canuto
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Validated
public class UserTokenRefreshUseCaseImpl implements UserTokenRefreshUseCase {

  private final UserLookUpUseCase userLookUpUseCase;
  private final UserTokenRenewalService userTokenRenewalService;
  private final RefreshTokenProcessor refreshTokenProcessor;

  /**
   * Constructs a new {@code UserTokenRefreshUseCaseImpl} with the required service dependencies.
   *
   * @param userLookUpUseCase the use case for retrieving user details
   * @param userTokenRenewalService the service responsible for issuing renewed tokens
   * @param refreshTokenProcessor the component for validating and parsing JWT refresh tokens
   */
  public UserTokenRefreshUseCaseImpl(
      UserLookUpUseCase userLookUpUseCase,
      UserTokenRenewalService userTokenRenewalService,
      RefreshTokenProcessor refreshTokenProcessor) {
    this.userLookUpUseCase = userLookUpUseCase;
    this.userTokenRenewalService = userTokenRenewalService;
    this.refreshTokenProcessor = refreshTokenProcessor;
  }

  /**
   * Refreshes the user's authentication tokens using the provided refresh token.
   *
   * <p>The method performs the following steps:
   *
   * <ol>
   *   <li>Validates and decodes the provided refresh token.
   *   <li>Retrieves the corresponding user entity and verifies its active status.
   *   <li>Regenerates access and refresh tokens for the user.
   *   <li>Returns a structured response containing updated credentials.
   * </ol>
   *
   * @param tokenRequest the incoming request containing the refresh token; must not be {@code null}
   * @return a {@link UserLoginDTO} containing the renewed access and refresh tokens
   * @throws org.springframework.security.authentication.BadCredentialsException if the provided
   *     token is invalid, malformed, or not a refresh token
   * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if no user
   *     exists for the username embedded in the token
   * @throws org.springframework.security.authentication.LockedException if the user's account is
   *     locked
   * @throws org.springframework.security.authentication.DisabledException if the user's account is
   *     inactive
   */
  @Override
  public UserLoginDTO refresh(@Valid @Nonnull TokenRequest tokenRequest) {

    // Extract the refresh token from the request
    final var refreshToken = tokenRequest.token();

    // Process and validate the refresh token
    final RefreshTokenProcessor.RefreshTokenData refreshTokenData =
        refreshTokenProcessor.process(refreshToken);

    try {
      // Retrieve the user or throw an exception if not found
      final var existingUser = userLookUpUseCase.findByUsername(refreshTokenData.username());

      // Verify that the user account is active
      existingUser.ensureActive();

      // Generate a new token set
      final var userToken =
          userTokenRenewalService.renewTokens(existingUser, refreshTokenData.jti());

      // Return the composed response DTO
      return UserLoginDTO.ofAccountAndToken(existingUser, userToken);

    } catch (UserNotFoundException ex) {
      throw SecurityExceptionFactory.UsernameNotFoundExceptionFactory.ofUsername(
          refreshTokenData.username());
    }
  }
}
