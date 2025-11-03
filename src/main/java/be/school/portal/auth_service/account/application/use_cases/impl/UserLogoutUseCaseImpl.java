package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.account.application.internal.services.UserTokenRevokeService;
import be.school.portal.auth_service.account.application.port.UserCachingPort;
import be.school.portal.auth_service.account.application.use_cases.UserLogoutUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import be.school.portal.auth_service.account.domain.exception.UserNotFoundException;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.common.component.RefreshTokenProcessor;
import be.school.portal.auth_service.common.dto.TokenRequest;
import be.school.portal.auth_service.common.utils.SecurityContextUtil;
import jakarta.annotation.Nonnull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation responsible for processing user logout operations.
 *
 * <p>This component revokes the user's refresh token, ensuring that it can no longer be used for
 * session renewal. The operation includes validation of the token structure, identity verification,
 * and proper cache eviction to maintain data consistency.
 *
 * <p>Transactional propagation is set to {@code REQUIRES_NEW} to ensure isolation of the logout
 * process from other ongoing transactions. Execution is performed asynchronously to enhance
 * throughput in high-load environments.
 *
 * <p>Primary responsibilities:
 *
 * <ul>
 *   <li>Validate and decode the refresh token.
 *   <li>Verify that the token owner matches the authenticated user.
 *   <li>Revoke the associated token and remove cached user data.
 * </ul>
 *
 * <p>This implementation adheres to the application service (use case) layer responsibilities
 * defined in the domain-driven design architecture.
 *
 * @see UserLookUpUseCase
 * @see UserTokenRevokeService
 * @see RefreshTokenProcessor
 * @see SecurityContextUtil
 * @author Francis Jorell J. Canuto
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserLogoutUseCaseImpl implements UserLogoutUseCase {

  private final UserLookUpUseCase userLookUpUseCase;
  private final UserTokenRevokeService userTokenRevokeService;
  private final RefreshTokenProcessor refreshTokenProcessor;
  private final UserCachingPort userCachingPort;

  /**
   * Constructs a new {@code UserLogoutUseCaseImpl} instance with required dependencies.
   *
   * @param userLookUpUseCase the use case for querying user details
   * @param userTokenRevokeService the domain service for revoking issued tokens
   * @param refreshTokenProcessor the component responsible for parsing and validating refresh
   *     tokens
   * @param userCachingPort the port for managing cached user information
   */
  public UserLogoutUseCaseImpl(
      UserLookUpUseCase userLookUpUseCase,
      UserTokenRevokeService userTokenRevokeService,
      RefreshTokenProcessor refreshTokenProcessor,
      UserCachingPort userCachingPort) {
    this.userLookUpUseCase = userLookUpUseCase;
    this.userTokenRevokeService = userTokenRevokeService;
    this.refreshTokenProcessor = refreshTokenProcessor;
    this.userCachingPort = userCachingPort;
  }

  /**
   * Performs the logout operation by revoking the user's refresh token.
   *
   * <p>This method ensures that:
   *
   * <ul>
   *   <li>The provided token is a valid refresh token.
   *   <li>The token's subject (username) matches the currently authenticated user.
   *   <li>The token is revoked, and related cache entries are invalidated.
   * </ul>
   *
   * <p>Execution is asynchronous to prevent blocking the caller during token revocation.
   *
   * @param tokenRequest the request object containing the refresh token to revoke
   * @throws org.springframework.security.authentication.BadCredentialsException if the provided
   *     token is malformed or not a refresh token
   * @throws org.springframework.security.access.AccessDeniedException if the token's username does
   *     not match the authenticated principal
   * @throws UsernameNotFoundException if the associated user account cannot be found
   */
  @Override
  @Async
  public void logout(@Nonnull TokenRequest tokenRequest) {

    // Extract token from the request
    final var refreshToken = tokenRequest.token();

    // Validate and process the refresh token
    final var refreshTokenData = refreshTokenProcessor.process(refreshToken);

    // Retrieve the currently authenticated username
    final var authenticatedUser = SecurityContextUtil.getUsername();

    // Ensure that the token belongs to the authenticated user
    if (!authenticatedUser.equals(refreshTokenData.username())) {
      throw SecurityExceptionFactory.AccessDeniedExceptionFactory.forUserMismatch(
          refreshTokenData.username());
    }

    try {
      // Retrieve the user entity
      final var existingUser = userLookUpUseCase.findByUsername(authenticatedUser);

      // Revoke the refresh token
      userTokenRevokeService.revoke(existingUser, refreshTokenData.jti());

      // Invalidate user cache
      userCachingPort.evictUserCache(authenticatedUser);

    } catch (UserNotFoundException ex) {
      throw SecurityExceptionFactory.UsernameNotFoundExceptionFactory.ofUsername(authenticatedUser);
    }
  }
}
