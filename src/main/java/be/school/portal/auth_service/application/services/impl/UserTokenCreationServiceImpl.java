package be.school.portal.auth_service.application.services.impl;

import be.school.portal.auth_service.application.services.UserTokenCreationService;
import be.school.portal.auth_service.common.component.JwtTokenComponent;
import be.school.portal.auth_service.common.component.JwtUserDetails;
import be.school.portal.auth_service.common.utils.JtiUtil;
import be.school.portal.auth_service.common.utils.ZonedDateTimeUtil;
import be.school.portal.auth_service.domain.entities.RefreshToken;
import be.school.portal.auth_service.domain.entities.UserAccount;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserTokenCreationServiceImpl implements UserTokenCreationService {

  private final UserRepository userRepository;
  private final JwtTokenComponent jwtTokenComponent;
  private final String jtiSalt;
  private final long accessExpirationMs;
  private final long refreshExpirationMs;

  public UserTokenCreationServiceImpl(
      UserRepository userRepository,
      JwtTokenComponent jwtTokenComponent,
      @Value("${jwt.access-expiration}") long accessExpirationMs,
      @Value("${jwt.refresh-expiration}") long refreshExpirationMs,
      @Value("${jwt.jti-salt}") String jtiSalt) {
    this.userRepository = userRepository;
    this.jwtTokenComponent = jwtTokenComponent;
    this.jtiSalt = jtiSalt;
    this.accessExpirationMs = accessExpirationMs;
    this.refreshExpirationMs = refreshExpirationMs;
  }

  @Override
  public UserToken create(@Nonnull UserAccount userAccount) {

    // Create Access token
    final var jwtAccessToken =
        jwtTokenComponent.generateToken(
            new JwtUserDetails(userAccount.getUsername(), userAccount.getRoleNames()),
            accessExpirationMs,
            JwtTokenComponent.TokenType.ACCESS);

    var now = ZonedDateTime.now();

    var refreshToken =
        RefreshToken.RefreshTokenBuilder.aRefreshToken()
            .withExpiresAt(ZonedDateTimeUtil.plusMillis(now, refreshExpirationMs))
            .withIssuedAt(now)
            .build();

    userAccount.addRefreshToken(refreshToken);

    userRepository.save(userAccount);

    String jti = JtiUtil.createJti(refreshToken.getId(), jtiSalt);

    // Create Access token
    final var jwtRefreshToken =
        jwtTokenComponent.generateToken(
            JwtUserDetails.withUsername(userAccount.getUsername()),
            refreshExpirationMs,
            JwtTokenComponent.TokenType.REFRESH,
            jti);

    return new UserToken(jwtAccessToken, jwtRefreshToken);
  }
}
