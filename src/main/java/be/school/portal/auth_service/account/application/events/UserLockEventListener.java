package be.school.portal.auth_service.account.application.events;

import be.school.portal.auth_service.account.application.use_cases.UserLockUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Listener for user lock events. Handles locking a user account when a lock event is published. */
@Component
public class UserLockEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLockEventListener.class);

  private final UserLockUseCase userLockUseCase;

  /**
   * Constructs a new UserLockEventListener.
   *
   * @param userLockUseCase the use case for locking user accounts
   */
  public UserLockEventListener(UserLockUseCase userLockUseCase) {
    this.userLockUseCase = userLockUseCase;
  }

  /**
   * Handles the user lock event before the transaction is committed. Attempts to lock the user and
   * logs any failure.
   *
   * @param userLockEventDTO the event containing the username to lock
   */
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleEvent(UserLockEventDTO userLockEventDTO) {

    LOGGER.info("Locking user: {}", userLockEventDTO.username());

    userLockUseCase.lockUser(userLockEventDTO.username());
  }

  /**
   * Event DTO representing a user lock event.
   *
   * @param username the username to be locked
   */
  public record UserLockEventDTO(String username) {}
}
