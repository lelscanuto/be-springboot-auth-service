package be.school.portal.auth_service.application.services.impl;

import be.school.portal.auth_service.application.services.UserTokenRevokeService;
import be.school.portal.auth_service.common.utils.JtiUtil;
import be.school.portal.auth_service.domain.entities.UserAccount;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
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

  private final UserRepository userRepository;
  private final String jtiSalt;

  /**
   * Constructs a new UserTokenRevokeServiceImpl.
   *
   * @param userRepository the user repository for persistence operations
   * @param jtiSalt the salt used for decoding the JWT ID (JTI)
   */
  public UserTokenRevokeServiceImpl(
      UserRepository userRepository, @Value("${jwt.jti-salt}") String jtiSalt) {
    this.userRepository = userRepository;
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
    userRepository.saveAndFlush(existingUser);
  }
}
