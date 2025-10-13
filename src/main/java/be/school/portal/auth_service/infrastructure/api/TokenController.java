package be.school.portal.auth_service.infrastructure.api;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.domain.projections.UserProjection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Token", description = "Endpoints for refresh, revoke, and extract token info")
public interface TokenController {

  @Operation(summary = "Refresh access token using a refresh token")
  CompletableFuture<LoginResponse> refresh(TokenRequest tokenRequest);

  @Operation(summary = "Revoke a refresh token")
  CompletableFuture<Void> revoke(TokenRequest tokenRequest);

  @Operation(summary = "Extract user info from a token")
  CompletableFuture<UserProjection> extract(TokenRequest tokenRequest);
}
