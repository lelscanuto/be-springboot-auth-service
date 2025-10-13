package be.school.portal.auth_service.infrastructure.api.impl;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.infrastructure.api.AuthenticationController;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationControllerImpl implements AuthenticationController {

  private final UserLoginUseCase userLoginUseCase;

  public AuthenticationControllerImpl(UserLoginUseCase userLoginUseCase) {
    this.userLoginUseCase = userLoginUseCase;
  }

  @Override
  @PostMapping("/login")
  public CompletableFuture<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return userLoginUseCase.login(loginRequest);
  }
}
