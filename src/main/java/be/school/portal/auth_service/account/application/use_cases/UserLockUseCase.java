package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.domain.projections.UserProjection;
import java.util.concurrent.CompletableFuture;

public interface UserLockUseCase {
  CompletableFuture<UserProjection> lockUser(String username);
}
