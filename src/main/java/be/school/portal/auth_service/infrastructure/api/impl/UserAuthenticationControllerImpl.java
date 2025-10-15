package be.school.portal.auth_service.infrastructure.api.impl;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.facade.UserAuthenticationFacade;
import be.school.portal.auth_service.infrastructure.api.UserAuthenticationController;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserAuthenticationControllerImpl implements UserAuthenticationController {

  private final UserAuthenticationFacade userAuthenticationFacade;

  public UserAuthenticationControllerImpl(UserAuthenticationFacade userAuthenticationFacade) {
    this.userAuthenticationFacade = userAuthenticationFacade;
  }

  @Override
  @PostMapping("/login")
  public CompletableFuture<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return userAuthenticationFacade.login(loginRequest);
  }

  @Override
  @PostMapping("/logout")
  public CompletableFuture<Void> logout(@RequestBody TokenRequest tokenRequest) {
    return userAuthenticationFacade.logout(tokenRequest);
  }

  @Override
  @PostMapping("/token/refresh")
  public CompletableFuture<LoginResponse> refresh(@RequestBody TokenRequest tokenRequest) {
    return userAuthenticationFacade.refreshToken(tokenRequest);
  }
}
