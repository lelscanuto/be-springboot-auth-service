package be.school.portal.auth_service.application.listener;

import be.school.portal.auth_service.application.use_cases.UserLockUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserLockEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLockEventListener.class);

  private final UserLockUseCase userLockUseCase;

  public UserLockEventListener(UserLockUseCase userLockUseCase) {
    this.userLockUseCase = userLockUseCase;
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleEvent(UserLockEventDTO userLockEventDTO) {
    userLockUseCase
        .lockUser(userLockEventDTO.username())
        .exceptionally(
            error -> {
              LOGGER.debug("Failed to lock user: {}", userLockEventDTO.username(), error);
              return null;
            });
  }

  public record UserLockEventDTO(String username) {}
}
