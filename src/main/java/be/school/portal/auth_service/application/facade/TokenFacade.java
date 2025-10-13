package be.school.portal.auth_service.application.facade;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.domain.projections.UserProjection;
import java.util.concurrent.CompletableFuture;

public interface TokenFacade {
  CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest);

  CompletableFuture<Void> revokeToken(TokenRequest tokenRequest);

  CompletableFuture<UserProjection> extractDetails(TokenRequest tokenRequest);
}
