package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.services.UserTokenRevokeService;
import be.school.portal.auth_service.application.use_cases.UserTokenRevokeUseCase;
import be.school.portal.auth_service.common.component.RefreshTokenProcessor;
import be.school.portal.auth_service.common.exceptions.InvalidCredentialException;
import be.school.portal.auth_service.common.exceptions.UnauthorizedException;
import be.school.portal.auth_service.common.exceptions.UserNotFoundException;
import be.school.portal.auth_service.common.utils.SecurityContextUtil;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
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

  private final UserRepository userRepository;
  private final UserTokenRevokeService userTokenRevokeService;
  private final RefreshTokenProcessor refreshTokenProcessor;

  /**
   * Constructs a new UserTokenRevokeUseCaseImpl.
   *
   * @param userTokenRevokeService the service handling token revocation logic
   * @param refreshTokenProcessor the component for JWT token operations
   */
  public UserTokenRevokeUseCaseImpl(
      UserRepository userRepository,
      UserTokenRevokeService userTokenRevokeService,
      RefreshTokenProcessor refreshTokenProcessor) {
    this.userRepository = userRepository;
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
   * @throws InvalidCredentialException if the token is invalid or not a refresh token
   * @throws UnauthorizedException if the token's username does not match the authenticated user
   */
  @Override
  @Async
  public CompletableFuture<Void> revoke(@Nonnull TokenRequest tokenRequest) {

    // Extract token from request
    final var refreshToken = tokenRequest.token();

    // Process and validate the refresh token
    final RefreshTokenProcessor.RefreshTokenData refreshTokenData =
        refreshTokenProcessor.process(refreshToken);

    // Ensure token owner matches the authenticated user
    if (!SecurityContextUtil.getUsername().equals(refreshTokenData.username())) {
      throw UnauthorizedException.ofUserMismatch(refreshTokenData.username());
    }

    final var authenticatedUser = SecurityContextUtil.getUsername();

    final var existingUser =
        userRepository
            .findByUsername(SecurityContextUtil.getUsername())
            .orElseThrow(() -> UserNotFoundException.ofUsername(authenticatedUser));

    // Revoke the token
    userTokenRevokeService.revoke(existingUser, refreshTokenData.jti());

    return CompletableFuture.completedFuture(null);
  }
}
