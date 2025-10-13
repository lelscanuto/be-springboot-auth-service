package be.school.portal.auth_service.application.facade.impl;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.facade.TokenFacade;
import be.school.portal.auth_service.application.use_cases.UserTokenExtractUseCase;
import be.school.portal.auth_service.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.application.use_cases.UserTokenRevokeUseCase;
import be.school.portal.auth_service.domain.projections.UserProjection;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class TokenFacadeImpl implements TokenFacade {

  private final UserTokenRefreshUseCase userTokenRefreshUseCase;
  private final UserTokenExtractUseCase userTokenExtractUseCase;
  private final UserTokenRevokeUseCase userTokenRevokeUseCase;

  public TokenFacadeImpl(
      UserTokenRefreshUseCase userTokenRefreshUseCase,
      UserTokenExtractUseCase userTokenExtractUseCase,
      UserTokenRevokeUseCase userTokenRevokeUseCase) {
    this.userTokenRefreshUseCase = userTokenRefreshUseCase;
    this.userTokenExtractUseCase = userTokenExtractUseCase;
    this.userTokenRevokeUseCase = userTokenRevokeUseCase;
  }

  @Override
  public CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest) {
    return userTokenRefreshUseCase.refresh(tokenRequest);
  }

  @Override
  public CompletableFuture<Void> revokeToken(TokenRequest tokenRequest) {
    return userTokenRevokeUseCase.revoke(tokenRequest);
  }

  @Override
  public CompletableFuture<UserProjection> extractDetails(TokenRequest tokenRequest) {
    return userTokenExtractUseCase.extractDetails(tokenRequest);
  }
}
