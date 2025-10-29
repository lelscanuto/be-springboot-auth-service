package be.school.portal.auth_service.account.application.internal.services.impl;

import be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService;
import be.school.portal.auth_service.common.component.JwtTokenComponent;
import be.school.portal.auth_service.common.component.JwtUserDetails;
import be.school.portal.auth_service.common.utils.JtiUtil;
import be.school.portal.auth_service.common.utils.ZonedDateTimeUtil;
import be.school.portal.auth_service.account.domain.entities.RefreshToken;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.infrastructure.repositories.RefreshTokenRepository;
import jakarta.annotation.Nonnull;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for creating JWT access and refresh tokens for a user. Handles token
 * generation, refresh token persistence, and JTI creation.
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserTokenCreationServiceImpl implements UserTokenCreationService {

  private final JwtTokenComponent jwtTokenComponent;
  private final RefreshTokenRepository refreshTokenRepository;
  private final String jtiSalt;
  private final long accessExpirationMs;
  private final long refreshExpirationMs;

  /**
   * Constructs a new UserTokenCreationServiceImpl.
   *
   * @param refreshTokenRepository the refresh token repository
   * @param jwtTokenComponent the component for JWT token operations
   * @param accessExpirationMs access token expiration in milliseconds
   * @param refreshExpirationMs refresh token expiration in milliseconds
   * @param jtiSalt the salt used for generating the JWT ID (JTI)
   */
  public UserTokenCreationServiceImpl(
      JwtTokenComponent jwtTokenComponent,
      RefreshTokenRepository refreshTokenRepository,
      @Value("${jwt.access-expiration}") long accessExpirationMs,
      @Value("${jwt.refresh-expiration}") long refreshExpirationMs,
      @Value("${jwt.jti-salt}") String jtiSalt) {
    this.jwtTokenComponent = jwtTokenComponent;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jtiSalt = jtiSalt;
    this.accessExpirationMs = accessExpirationMs;
    this.refreshExpirationMs = refreshExpirationMs;
  }

  /**
   * Creates new access and refresh tokens for the given user account. Persists the new refresh
   * token and generates a JTI for it.
   *
   * @param userAccount the user account for which to create tokens
   * @return a {@link UserToken} containing the generated access and refresh tokens
   */
  @Override
  public UserToken create(@Nonnull UserAccount userAccount) {

    // Create Access token
    final var jwtAccessToken =
        jwtTokenComponent.generateToken(
            new JwtUserDetails(
                userAccount.getId(), userAccount.getUsername(), userAccount.getRoleNames()),
            accessExpirationMs,
            JwtTokenComponent.TokenType.ACCESS);

    // Current time
    final var now = ZonedDateTime.now();

    // Create Refresh token
    final var refreshToken =
        RefreshToken.RefreshTokenBuilder.aRefreshToken()
            .withExpiresAt(ZonedDateTimeUtil.plusMillis(now, refreshExpirationMs))
            .withIssuedAt(now)
            .build();

    // Add refresh token to user
    userAccount.addRefreshToken(refreshToken);

    // Persist user with new refresh token
    refreshTokenRepository.save(refreshToken);

    // Create JTI for refresh token
    final var jti = JtiUtil.createJti(refreshToken.getId(), jtiSalt);

    // Create Refresh token
    final var jwtRefreshToken =
        jwtTokenComponent.generateToken(
            JwtUserDetails.withUserIdAndUsername(userAccount.getId(), userAccount.getUsername()),
            refreshExpirationMs,
            JwtTokenComponent.TokenType.REFRESH,
            jti);

    // Return tokens
    return new UserToken(jwtAccessToken, jwtRefreshToken);
  }
}
