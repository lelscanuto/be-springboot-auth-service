package be.school.portal.auth_service.account.application.adapters;

import be.school.portal.auth_service.account.application.dto.LoginRequest;
import be.school.portal.auth_service.account.application.dto.LoginResponse;
import be.school.portal.auth_service.account.application.dto.TokenRequest;
import be.school.portal.auth_service.account.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRevokeUseCase;
import be.school.portal.auth_service.account.presentation.facade.UserAuthenticationFacade;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationAdapter implements UserAuthenticationFacade {

  private final UserLoginUseCase userLoginUseCase;
  private final UserTokenRefreshUseCase userTokenRefreshUseCase;
  private final UserTokenRevokeUseCase userTokenRevokeUseCase;

  public UserAuthenticationAdapter(
      UserLoginUseCase userLoginUseCase,
      UserTokenRefreshUseCase userTokenRefreshUseCase,
      UserTokenRevokeUseCase userTokenRevokeUseCase) {
    this.userLoginUseCase = userLoginUseCase;
    this.userTokenRefreshUseCase = userTokenRefreshUseCase;
    this.userTokenRevokeUseCase = userTokenRevokeUseCase;
  }

  @Override
  public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {
    return userLoginUseCase.login(loginRequest);
  }

  @Override
  public CompletableFuture<Void> logout(TokenRequest tokenRequest) {
    return userTokenRevokeUseCase.revoke(tokenRequest);
  }

  @Override
  public CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest) {
    return userTokenRefreshUseCase.refresh(tokenRequest);
  }
}
