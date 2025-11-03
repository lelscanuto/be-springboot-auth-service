package be.school.portal.auth_service.account.application.internal.services.impl;

import be.school.portal.auth_service.account.application.events.UserLockEventListener;
import be.school.portal.auth_service.account.application.internal.services.LoginAttemptService;
import be.school.portal.auth_service.account.domain.entities.UserLoginAudit;
import be.school.portal.auth_service.account.domain.enums.LoginAction;
import be.school.portal.auth_service.account.infrastructure.repositories.UserLoginAuditRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation responsible for tracking, auditing, and enforcing login attempt policies.
 *
 * <p>This component monitors user authentication attempts and enforces account locking after a
 * configurable number of consecutive failed login attempts within a defined time window. It is
 * primarily used to mitigate brute-force attacks and strengthen account-level security.
 *
 * <p>The service operates in a new transactional boundary ({@link
 * org.springframework.transaction.annotation.Propagation#REQUIRES_NEW}) to ensure audit persistence
 * even when the parent authentication transaction fails or rolls back.
 *
 * <p>Once the maximum allowed failed attempts is reached, a {@link
 * be.school.portal.auth_service.account.application.events.UserLockEventListener.UserLockEventDTO}
 * is published via the {@link ApplicationEventPublisher}, allowing listeners (e.g., {@link
 * be.school.portal.auth_service.account.application.events.UserLockEventListener}) to handle user
 * lock enforcement asynchronously.
 *
 * <p>Default policy configuration:
 *
 * <ul>
 *   <li>Maximum consecutive failed attempts: {@value #MAX_ATTEMPTS}
 *   <li>Observation window: {@value #BLOCK_TIME_MINUTES} minutes
 * </ul>
 *
 * @see be.school.portal.auth_service.account.domain.entities.UserLoginAudit
 * @see be.school.portal.auth_service.account.domain.enums.LoginAction
 * @see be.school.portal.auth_service.account.application.events.UserLockEventListener
 * @see org.springframework.transaction.annotation.Transactional
 * @author Francis Jorell Canuto
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LoginAttemptServiceImpl implements LoginAttemptService {

  /** Maximum number of allowed consecutive failed login attempts. */
  private static final int MAX_ATTEMPTS = 5;

  /** Duration (in minutes) of the observation window used for counting failed login attempts. */
  private static final int BLOCK_TIME_MINUTES = 15;

  private final UserLoginAuditRepository userLoginAuditRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Constructs a new {@code LoginAttemptServiceImpl} instance.
   *
   * @param userLoginAuditRepository the repository responsible for persisting login attempt audit
   *     records
   * @param applicationEventPublisher the Spring event publisher for dispatching user lock events
   */
  public LoginAttemptServiceImpl(
      UserLoginAuditRepository userLoginAuditRepository,
      ApplicationEventPublisher applicationEventPublisher) {
    this.userLoginAuditRepository = userLoginAuditRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  /**
   * Records a failed login attempt for a given user and evaluates whether the account should be
   * temporarily locked.
   *
   * <p>The service performs the following steps:
   *
   * <ol>
   *   <li>Persists the failed login attempt to the audit repository.
   *   <li>Retrieves the user’s recent login attempts within the observation window.
   *   <li>Counts consecutive failed attempts, resetting the counter on any successful login.
   *   <li>If the failure threshold is reached, publishes a {@code UserLockEventDTO} via the Spring
   *       event system.
   * </ol>
   *
   * @param username the username associated with the failed login attempt
   * @param loginAction the login action performed (typically {@link
   *     be.school.portal.auth_service.account.domain.enums.LoginAction#LOGIN_FAILED_INVALID_CREDENTIAL})
   * @see be.school.portal.auth_service.account.application.events.UserLockEventListener
   */
  @Override
  public void recordFailure(String username, LoginAction loginAction) {

    userLoginAuditRepository.saveAndFlush(
        UserLoginAudit.builder().withAction(loginAction).withUsername(username).build());

    ZonedDateTime from = ZonedDateTime.now().minusMinutes(BLOCK_TIME_MINUTES);
    ZonedDateTime to = ZonedDateTime.now();

    // Retrieve relevant attempts (failed or successful) within the observation window
    List<UserLoginAudit> recentRelevant =
        userLoginAuditRepository
            .findByUsernameAndActionInAndAttemptedAtBetweenOrderByAttemptedAtAsc(
                username,
                List.of(LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL, LoginAction.LOGIN_SUCCESS),
                from,
                to,
                PageRequest.of(0, MAX_ATTEMPTS));

    int consecutiveFails = 0;

    // Count consecutive failed attempts
    for (UserLoginAudit attempt : recentRelevant) {
      if (attempt.getAction() == LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL) {
        consecutiveFails++;
      } else {
        // Reset counter on successful login
        consecutiveFails = 0;
      }
    }

    // If threshold reached, publish lock event
    if (consecutiveFails >= MAX_ATTEMPTS) {
      applicationEventPublisher.publishEvent(new UserLockEventListener.UserLockEventDTO(username));
    }
  }

  /**
   * Records a successful login attempt for the specified user.
   *
   * <p>Successful attempts are also persisted to the audit log to reset the failure counter and
   * maintain a complete authentication audit trail.
   *
   * @param username the username associated with the successful login attempt
   * @param loginAction the login action performed (typically {@link
   *     be.school.portal.auth_service.account.domain.enums.LoginAction#LOGIN_SUCCESS})
   */
  @Override
  public void recordSuccess(String username, LoginAction loginAction) {
    userLoginAuditRepository.save(
        UserLoginAudit.builder().withAction(loginAction).withUsername(username).build());
  }
}
