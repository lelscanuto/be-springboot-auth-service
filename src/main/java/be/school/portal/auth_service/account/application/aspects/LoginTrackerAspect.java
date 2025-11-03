package be.school.portal.auth_service.account.application.aspects;

import be.school.portal.auth_service.account.application.internal.services.LoginAttemptService;
import be.school.portal.auth_service.account.domain.enums.LoginAction;
import be.school.portal.auth_service.common.dto.LoginRequest;
import java.util.Optional;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

/**
 * Aspect responsible for tracking and auditing user login outcomes using AOP-based interception.
 *
 * <p>This component monitors methods annotated with {@link
 * be.school.portal.auth_service.account.application.aspects.TrackLogin} and records whether login
 * attempts succeed or fail. It acts as a centralized, cross-cutting concern for login auditing,
 * decoupling authentication logic from security event tracking.
 *
 * <p>On successful authentication, the aspect records a {@link
 * be.school.portal.auth_service.account.domain.enums.LoginAction#LOGIN_SUCCESS}. On authentication
 * failure, it classifies the exception type and records the corresponding failure reason, such as:
 *
 * <ul>
 *   <li>{@link
 *       be.school.portal.auth_service.account.domain.enums.LoginAction#LOGIN_FAILED_INVALID_CREDENTIAL}
 *       — when authentication fails due to incorrect credentials
 *   <li>{@link
 *       be.school.portal.auth_service.account.domain.enums.LoginAction#LOGIN_FAILED_INVALID_STATE}
 *       — when authentication fails because the account is disabled or locked
 * </ul>
 *
 * <p>All login attempts are delegated to {@link LoginAttemptService}, which manages audit
 * persistence and user lock policy enforcement.
 *
 * @see be.school.portal.auth_service.account.application.aspects.TrackLogin
 * @see be.school.portal.auth_service.account.domain.enums.LoginAction
 * @see be.school.portal.auth_service.account.application.internal.services.LoginAttemptService
 * @author Francis Jorell Canuto
 */
@Aspect
@Component
public class LoginTrackerAspect {

  private final LoginAttemptService loginAttemptService;

  /**
   * Constructs a new {@code LoginTrackerAspect}.
   *
   * @param loginAttemptService the service responsible for recording login attempt outcomes and
   *     enforcing lockout policies
   */
  public LoginTrackerAspect(LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }

  /**
   * Determines the corresponding {@link LoginAction} for a given authentication exception.
   *
   * <p>This method inspects the thrown exception type and maps it to the appropriate audit action:
   *
   * <ul>
   *   <li>{@link BadCredentialsException} → {@code LOGIN_FAILED_INVALID_CREDENTIAL}
   *   <li>{@link LockedException} or {@link DisabledException} → {@code LOGIN_FAILED_INVALID_STATE}
   * </ul>
   *
   * @param ex the exception thrown during login
   * @return an {@link Optional} containing the resolved {@link LoginAction}, or an empty value if
   *     the exception type is not tracked
   */
  private static Optional<LoginAction> getLoginActionFromException(Exception ex) {
    if (ex instanceof BadCredentialsException) {
      return Optional.of(LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL);
    } else if (ex instanceof LockedException || ex instanceof DisabledException) {
      return Optional.of(LoginAction.LOGIN_FAILED_INVALID_STATE);
    }
    return Optional.empty();
  }

  /**
   * Advice executed after a successful login attempt on a method annotated with {@link
   * be.school.portal.auth_service.account.application.aspects.TrackLogin}.
   *
   * <p>Records a {@link LoginAction#LOGIN_SUCCESS} event for the given username.
   *
   * @param loginRequest the login request object containing the username and credentials
   */
  @AfterReturning(
      value =
          "@annotation(be.school.portal.auth_service.account.application.aspects.TrackLogin) && args(loginRequest,..)")
  public void afterSuccess(LoginRequest loginRequest) {
    loginAttemptService.recordSuccess(loginRequest.username(), LoginAction.LOGIN_SUCCESS);
  }

  /**
   * Advice executed after a failed login attempt on a method annotated with {@link
   * be.school.portal.auth_service.account.application.aspects.TrackLogin}.
   *
   * <p>This advice determines the nature of the failure based on the thrown exception and records
   * the corresponding {@link LoginAction}. Examples include failed credentials or disabled account
   * states.
   *
   * @param loginRequest the login request object containing the username and credentials
   * @param ex the exception thrown during authentication
   */
  @AfterThrowing(
      value =
          "@annotation(be.school.portal.auth_service.account.application.aspects.TrackLogin) && args(loginRequest,..)",
      throwing = "ex")
  public void afterFailure(LoginRequest loginRequest, Exception ex) {
    getLoginActionFromException(ex)
        .ifPresent(action -> loginAttemptService.recordFailure(loginRequest.username(), action));
  }
}
