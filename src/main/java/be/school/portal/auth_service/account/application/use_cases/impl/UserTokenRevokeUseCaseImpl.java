package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.common.dto.TokenRequest;
import be.school.portal.auth_service.account.application.internal.services.UserTokenRevokeService;
import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRevokeUseCase;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.common.component.RefreshTokenProcessor;
import be.school.portal.auth_service.common.utils.SecurityContextUtil;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for revoking a user's refresh token.
 *
 * <p>This class validates the provided token, ensures it is a refresh token, extracts the username
 * and JTI, and delegates the revocation process to the service layer.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserTokenRevokeUseCaseImpl implements UserTokenRevokeUseCase {

  private final UserRepositoryPort userRepositoryPort;
  private final UserTokenRevokeService userTokenRevokeService;
  private final RefreshTokenProcessor refreshTokenProcessor;

  /**
   * Constructs a new UserTokenRevokeUseCaseImpl.
   *
   * @param userTokenRevokeService the service handling token revocation logic
   * @param refreshTokenProcessor the component for JWT token operations
   */
  public UserTokenRevokeUseCaseImpl(
      UserRepositoryPort userRepositoryPort,
      UserTokenRevokeService userTokenRevokeService,
      RefreshTokenProcessor refreshTokenProcessor) {
    this.userRepositoryPort = userRepositoryPort;
    this.userTokenRevokeService = userTokenRevokeService;
    this.refreshTokenProcessor = refreshTokenProcessor;
  }

  /**
   * Revokes the refresh token provided in the token request.
   *
   * <ul>
   *   <li>Validates the token structure
   *   <li>Ensures the token is a refresh token
   *   <li>Extracts the username and JTI from the token
   *   <li>Delegates the revocation to the service layer
   * </ul>
   *
   * @param tokenRequest the request containing the token to revoke
   * @return a completed {@link CompletableFuture} when revocation is done
   * @throws org.springframework.security.authentication.BadCredentialsException if the token is
   *     invalid or not a refresh token
   * @throws org.springframework.security.access.AccessDeniedException if the token's username does
   *     not match the authenticated user
   * @throws UsernameNotFoundException if authenticated user is not found
   */
  @Override
  @Async
  public CompletableFuture<Void> revoke(@Nonnull TokenRequest tokenRequest) {

    // Extract token from request
    final var refreshToken = tokenRequest.token();

    // Process and validate the refresh token
    final RefreshTokenProcessor.RefreshTokenData refreshTokenData =
        refreshTokenProcessor.process(refreshToken);

    // Get Authenticated User
    final var authenticatedUser = SecurityContextUtil.getUsername();

    // Ensure token owner matches the authenticated user
    if (!authenticatedUser.equals(refreshTokenData.username())) {
      throw SecurityExceptionFactory.AccessDeniedExceptionFactory.forUserMismatch(
          refreshTokenData.username());
    }

    // Query User if it exists
    final var existingUser =
        userRepositoryPort
            .findByUsername(authenticatedUser)
            .orElseThrow(
                () ->
                    SecurityExceptionFactory.UsernameNotFoundExceptionFactory.ofUsername(
                        authenticatedUser));

    // Revoke the token
    userTokenRevokeService.revoke(existingUser, refreshTokenData.jti());

    return CompletableFuture.completedFuture(null);
  }
}
