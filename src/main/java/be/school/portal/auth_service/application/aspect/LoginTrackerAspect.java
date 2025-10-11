package be.school.portal.auth_service.application.aspect;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.services.LoginAttemptService;
import be.school.portal.auth_service.common.exceptions.InvalidPasswordException;
import be.school.portal.auth_service.common.exceptions.UserInvalidStateException;
import be.school.portal.auth_service.domain.enums.LoginAction;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoginTrackerAspect {

  private final LoginAttemptService loginAttemptService;

  public LoginTrackerAspect(LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }

  @AfterReturning(value = "@annotation(trackLogin) && args(loginRequest,..)")
  public void afterSuccess(LoginRequest loginRequest) {
    loginAttemptService.recordSuccess(loginRequest.username(), LoginAction.LOGIN_SUCCESS);
  }

  @AfterThrowing(value = "@annotation(trackLogin) && args(loginRequest,..)", throwing = "ex")
  public void afterFailure(LoginRequest loginRequest, InvalidPasswordException ex) {
    loginAttemptService.recordFailure(
        loginRequest.username(), LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL);
  }

  @AfterThrowing(value = "@annotation(trackLogin) && args(loginRequest,..)", throwing = "ex")
  public void afterUserNotValid(LoginRequest loginRequest, UserInvalidStateException ex) {
    loginAttemptService.recordFailure(
        loginRequest.username(), LoginAction.LOGIN_FAILED_INVALID_STATE);
  }
}
