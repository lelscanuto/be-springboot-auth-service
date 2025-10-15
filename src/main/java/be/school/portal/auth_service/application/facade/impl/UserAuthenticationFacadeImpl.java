package be.school.portal.auth_service.application.facade.impl;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.facade.UserAuthenticationFacade;
import be.school.portal.auth_service.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.application.use_cases.UserTokenRevokeUseCase;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationFacadeImpl implements UserAuthenticationFacade {

  private final UserLoginUseCase userLoginUseCase;
  private final UserTokenRefreshUseCase userTokenRefreshUseCase;
  private final UserTokenRevokeUseCase userTokenRevokeUseCase;

  public UserAuthenticationFacadeImpl(
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
