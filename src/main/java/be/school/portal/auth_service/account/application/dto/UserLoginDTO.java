package be.school.portal.auth_service.account.application.dto;

import be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import java.util.Set;

public record UserLoginDTO(
    String username, String accessToken, String refreshToken, Set<String> roles) {

  public static UserLoginDTO ofAccountAndToken(
      UserAccount userAccount, UserTokenCreationService.UserToken token) {
    return new UserLoginDTO(
        userAccount.getUsername(),
        token.accessToken(),
        token.refreshToken(),
        userAccount.getRoleNames());
  }
}
