package be.school.portal.auth_service.application.facade;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import java.util.concurrent.CompletableFuture;

public interface UserAuthenticationFacade {

  CompletableFuture<LoginResponse> login(LoginRequest loginRequest);

  CompletableFuture<Void> logout(TokenRequest tokenRequest);

  CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest);
}
