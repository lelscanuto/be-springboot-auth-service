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

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LoginAttemptServiceImpl implements LoginAttemptService {

  private static final int MAX_ATTEMPTS = 5;
  private static final int BLOCK_TIME_MINUTES = 15;

  private final UserLoginAuditRepository userLoginAuditRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  public LoginAttemptServiceImpl(
      UserLoginAuditRepository userLoginAuditRepository,
      ApplicationEventPublisher applicationEventPublisher) {
    this.userLoginAuditRepository = userLoginAuditRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

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

  @Override
  public void recordSuccess(String username, LoginAction loginAction) {
    userLoginAuditRepository.save(
        UserLoginAudit.builder().withAction(loginAction).withUsername(username).build());
  }
}
