package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.services.UserTokenRevokeService;
import be.school.portal.auth_service.application.use_cases.UserTokenRevokeUseCase;
import be.school.portal.auth_service.common.component.JwtTokenComponent;
import be.school.portal.auth_service.common.exceptions.InvalidCredentialException;
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
  private final JwtTokenComponent jwtTokenComponent;

  /**
   * Constructs a new UserTokenRevokeUseCaseImpl.
   *
   * @param userRepository the user repository for user lookup
   * @param userTokenRevokeService the service handling token revocation logic
   * @param jwtTokenComponent the component for JWT token operations
   */
  public UserTokenRevokeUseCaseImpl(
      UserRepository userRepository,
      UserTokenRevokeService userTokenRevokeService,
      JwtTokenComponent jwtTokenComponent) {
    this.userRepository = userRepository;
    this.userTokenRevokeService = userTokenRevokeService;
    this.jwtTokenComponent = jwtTokenComponent;
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
   */
  @Override
  @Async
  public CompletableFuture<Void> revoke(@Nonnull TokenRequest tokenRequest) {

    // Extract token from request
    final var token = tokenRequest.token();

    // Validate token
    if (!jwtTokenComponent.validateToken(token)) {
      throw InvalidCredentialException.ofToken(token);
    }

    // Ensure it’s a refresh token (not access)
    JwtTokenComponent.TokenType tokenType = jwtTokenComponent.getTokenType(token);
    if (JwtTokenComponent.TokenType.REFRESH != tokenType) {
      throw InvalidCredentialException.ofTokenType(tokenType.name());
    }

    // Extract username and JTI
    final var jti = jwtTokenComponent.getJtiFromToken(token);
    final var username = jwtTokenComponent.getUsernameFromToken(token);

    // Lookup user
    final var existingUser =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> InvalidCredentialException.ofUsername(username));

    // Revoke the token
    userTokenRevokeService.revoke(existingUser, jti);

    return CompletableFuture.completedFuture(null);
  }
}
