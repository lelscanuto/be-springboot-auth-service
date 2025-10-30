package be.school.portal.auth_service.account.application.aspects;

import be.school.portal.auth_service.account.application.internal.services.LoginAttemptService;
import be.school.portal.auth_service.account.domain.enums.LoginAction;
import be.school.portal.auth_service.common.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoginTrackerAspect {

  private final LoginAttemptService loginAttemptService;

  public LoginTrackerAspect(LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }

  @AfterReturning(
      value =
          "@annotation( be.school.portal.auth_service.account.application.aspects.TrackLogin) && args(loginRequest,..)")
  public void afterSuccess(LoginRequest loginRequest) {
    loginAttemptService.recordSuccess(loginRequest.username(), LoginAction.LOGIN_SUCCESS);
  }

  @AfterThrowing(
      value =
          "@annotation( be.school.portal.auth_service.account.application.aspects.TrackLogin) && args(loginRequest,..)",
      throwing = "ex")
  public void afterFailure(LoginRequest loginRequest, Exception ex) {
    loginAttemptService.recordFailure(loginRequest.username(), getLoginActionFromException(ex));
  }

  private LoginAction getLoginActionFromException(Exception ex) {
    if (ex instanceof BadCredentialsException) {
      return LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL;
    } else if (ex instanceof LockedException || ex instanceof DisabledException) {
      return LoginAction.LOGIN_FAILED_INVALID_STATE;
    }

    throw new UnsupportedOperationException(" Unsupported exception type: " + ex.getClass());
  }
}
