package be.school.portal.auth_service.account.application.internal.services.impl;

import be.school.portal.auth_service.account.application.internal.services.UserTokenRevokeService;
import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.common.utils.JtiUtil;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service implementation for revoking a user's refresh token.
 *
 * <p>Decodes the JTI, revokes the corresponding refresh token from the user, and persists the
 * changes.
 */
@Service
public class UserTokenRevokeServiceImpl implements UserTokenRevokeService {

  private final UserRepositoryPort userRepositoryPort;
  private final String jtiSalt;

  /**
   * Constructs a new UserTokenRevokeServiceImpl.
   *
   * @param userRepositoryPort the user repository for persistence operations
   * @param jtiSalt the salt used for decoding the JWT ID (JTI)
   */
  public UserTokenRevokeServiceImpl(
      UserRepositoryPort userRepositoryPort, @Value("${jwt.jti-salt}") String jtiSalt) {
    this.userRepositoryPort = userRepositoryPort;
    this.jtiSalt = jtiSalt;
  }

  /**
   * Revokes the refresh token identified by the given JTI for the specified user. Persists the
   * updated user entity.
   *
   * @param existingUser the user whose token is to be revoked
   * @param jti the JWT ID of the refresh token to revoke
   */
  @Override
  public void revoke(@Nonnull UserAccount existingUser, @Nonnull String jti) {

    // Decode the JTI using the configured salt
    final var refreshTokenId = JtiUtil.decodeJti(jti, jtiSalt);

    // Revoke the token from the user entity
    existingUser.revokeTokenWith(refreshTokenId);

    // Persist the updated user entity
    userRepositoryPort.saveAndFlush(existingUser);
  }
}
