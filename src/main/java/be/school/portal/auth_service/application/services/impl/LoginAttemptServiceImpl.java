package be.school.portal.auth_service.application.services.impl;

import be.school.portal.auth_service.application.listener.UserLockEventListener;
import be.school.portal.auth_service.application.services.LoginAttemptService;
import be.school.portal.auth_service.domain.entities.UserLoginAudit;
import be.school.portal.auth_service.domain.enums.LoginAction;
import be.school.portal.auth_service.infrastructure.repositories.UserLoginAuditRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for tracking and handling user login attempts. Locks the user account
 * after a configurable number of consecutive failed attempts within a specified time window.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LoginAttemptServiceImpl implements LoginAttemptService {

  private static final int MAX_ATTEMPTS = 5;
  private static final int BLOCK_TIME_MINUTES = 15;

  private final UserLoginAuditRepository userLoginAuditRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Constructs a new LoginAttemptServiceImpl.
   *
   * @param userLoginAuditRepository the repository for user login audit records
   * @param applicationEventPublisher the event publisher for user lock events
   */
  public LoginAttemptServiceImpl(
      UserLoginAuditRepository userLoginAuditRepository,
      ApplicationEventPublisher applicationEventPublisher) {
    this.userLoginAuditRepository = userLoginAuditRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  /**
   * Records a failed login attempt for the specified user and action. If the number of consecutive
   * failures exceeds the maximum allowed, publishes a user lock event.
   *
   * @see UserLockEventListener
   * @param username the username of the user
   * @param loginAction the login action performed
   */
  @Override
  public void recordFailure(String username, LoginAction loginAction) {
    userLoginAuditRepository.saveAndFlush(
        UserLoginAudit.builder().withAction(loginAction).withUsername(username).build());

    ZonedDateTime from = ZonedDateTime.now().minusMinutes(BLOCK_TIME_MINUTES);
    ZonedDateTime to = ZonedDateTime.now();

    // Fetch recent relevant attempts (both failed and successful)
    List<UserLoginAudit> recentRelevant =
        userLoginAuditRepository
            .findByUsernameAndActionInAndAttemptedAtBetweenOrderByAttemptedAtAsc(
                username,
                List.of(LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL, LoginAction.LOGIN_SUCCESS),
                from,
                to,
                PageRequest.of(0, MAX_ATTEMPTS));

    int consecutiveFails = 0;

    // Count consecutive failures
    for (UserLoginAudit attempt : recentRelevant) {
      if (attempt.getAction() == LoginAction.LOGIN_FAILED_INVALID_CREDENTIAL) {
        consecutiveFails++;
      } else {
        // Reset on success
        consecutiveFails = 0;
      }
    }

    // If max attempts reached, publish lock event
    if (consecutiveFails >= MAX_ATTEMPTS) {
      applicationEventPublisher.publishEvent(new UserLockEventListener.UserLockEventDTO(username));
    }
  }

  /**
   * Records a successful login attempt for the specified user and action.
   *
   * @param username the username of the user
   * @param loginAction the login action performed
   */
  @Override
  public void recordSuccess(String username, LoginAction loginAction) {
    userLoginAuditRepository.save(
        UserLoginAudit.builder().withAction(loginAction).withUsername(username).build());
  }
}
