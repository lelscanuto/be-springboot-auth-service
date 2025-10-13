package be.school.portal.auth_service.infrastructure.api.impl;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.facade.TokenFacade;
import be.school.portal.auth_service.domain.projections.UserProjection;
import be.school.portal.auth_service.infrastructure.api.TokenController;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class TokenControllerImpl implements TokenController {

  private final TokenFacade tokenFacade;

  public TokenControllerImpl(TokenFacade tokenFacade) {
    this.tokenFacade = tokenFacade;
  }

  @PostMapping("/refresh-token")
  @Override
  public CompletableFuture<LoginResponse> refresh(@RequestBody TokenRequest tokenRequest) {
    return tokenFacade.refreshToken(tokenRequest);
  }

  @PostMapping("/revoke-token")
  @Override
  public CompletableFuture<Void> revoke(@RequestBody TokenRequest tokenRequest) {
    return tokenFacade.revokeToken(tokenRequest);
  }

  @PostMapping("/extract-token")
  @Override
  public CompletableFuture<UserProjection> extract(@RequestBody TokenRequest tokenRequest) {
    return tokenFacade.extractDetails(tokenRequest);
  }
}
