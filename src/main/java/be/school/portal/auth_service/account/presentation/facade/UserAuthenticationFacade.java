package be.school.portal.auth_service.account.presentation.facade;

import be.school.portal.auth_service.common.dto.LoginRequest;
import be.school.portal.auth_service.common.dto.LoginResponse;
import be.school.portal.auth_service.common.dto.TokenRequest;
import java.util.concurrent.CompletableFuture;

public interface UserAuthenticationFacade {

  CompletableFuture<LoginResponse> login(LoginRequest loginRequest);

  CompletableFuture<Void> logout(TokenRequest tokenRequest);

  CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest);
}
