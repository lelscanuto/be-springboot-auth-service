package be.school.portal.auth_service.account.presentation.api.impl;

import be.school.portal.auth_service.common.dto.LoginRequest;
import be.school.portal.auth_service.common.dto.LoginResponse;
import be.school.portal.auth_service.common.dto.TokenRequest;
import be.school.portal.auth_service.account.presentation.api.UserAuthenticationController;
import be.school.portal.auth_service.account.presentation.facade.UserAuthenticationFacade;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
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
