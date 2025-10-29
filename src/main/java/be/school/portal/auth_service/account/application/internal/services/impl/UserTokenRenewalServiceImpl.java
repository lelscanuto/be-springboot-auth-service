package be.school.portal.auth_service.account.application.internal.services.impl;

import be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService;
import be.school.portal.auth_service.account.application.internal.services.UserTokenRenewalService;
import be.school.portal.auth_service.account.application.internal.services.UserTokenRevokeService;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

/**
 * Service implementation for renewing user tokens.
 *
 * <p>This class handles the revocation of the old refresh token using its JTI and issues new access
 * and refresh tokens for the user.
 */
@Service
public class UserTokenRenewalServiceImpl implements UserTokenRenewalService {

  private final UserTokenRevokeService userTokenRevokeService;
  private final UserTokenCreationService userTokenCreationService;

  /**
   * Constructs a new UserTokenRenewalServiceImpl.
   *
   * @param userTokenRevokeService the service responsible for revoking user tokens
   * @param userTokenCreationService the service responsible for creating new user tokens
   */
  public UserTokenRenewalServiceImpl(
      UserTokenRevokeService userTokenRevokeService,
      UserTokenCreationService userTokenCreationService) {
    this.userTokenRevokeService = userTokenRevokeService;
    this.userTokenCreationService = userTokenCreationService;
  }

  /**
   * Renews the tokens for the given user by revoking the old refresh token (identified by JTI) and
   * issuing new tokens.
   *
   * @param existingUser the user whose tokens are to be renewed
   * @param jti the JWT ID of the refresh token to revoke
   * @return a new {@link UserTokenCreationService.UserToken} containing the new tokens
   */
  @Override
  public UserTokenCreationService.UserToken renewTokens(
      @Nonnull UserAccount existingUser, @Nonnull String jti) {

    // Revoke the old refresh token using its JTI
    userTokenRevokeService.revoke(existingUser, jti);

    // Create and return new tokens for the user
    return userTokenCreationService.create(existingUser);
  }
}
