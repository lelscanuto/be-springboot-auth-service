package be.school.portal.auth_service.account.application.adapters;

import be.school.portal.auth_service.account.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.account.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRevokeUseCase;
import be.school.portal.auth_service.account.presentation.facade.UserAuthenticationFacade;
import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.dto.LoginRequest;
import be.school.portal.auth_service.common.dto.LoginResponse;
import be.school.portal.auth_service.common.dto.TokenRequest;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Trace(logArgsAndResult = true)
public class AsyncUserAuthenticationApiAdapter implements UserAuthenticationFacade {

  private final UserLoginUseCase userLoginUseCase;
  private final UserTokenRefreshUseCase userTokenRefreshUseCase;
  private final UserTokenRevokeUseCase userTokenRevokeUseCase;
  private final LoginResponseMapper loginResponseMapper;

  public AsyncUserAuthenticationApiAdapter(
      UserLoginUseCase userLoginUseCase,
      UserTokenRefreshUseCase userTokenRefreshUseCase,
      UserTokenRevokeUseCase userTokenRevokeUseCase,
      LoginResponseMapper loginResponseMapper) {
    this.userLoginUseCase = userLoginUseCase;
    this.userTokenRefreshUseCase = userTokenRefreshUseCase;
    this.userTokenRevokeUseCase = userTokenRevokeUseCase;
    this.loginResponseMapper = loginResponseMapper;
  }

  @Override
  @Async
  public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {
    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(userLoginUseCase.login(loginRequest)));
  }

  @Override
  @Async
  public CompletableFuture<Void> logout(TokenRequest tokenRequest) {
    userTokenRevokeUseCase.revoke(tokenRequest);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest) {
    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(userTokenRefreshUseCase.refresh(tokenRequest)));
  }
}
