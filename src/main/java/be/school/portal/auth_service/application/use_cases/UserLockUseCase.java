package be.school.portal.auth_service.application.use_cases;

import be.school.portal.auth_service.domain.projections.UserProjection;
import java.util.concurrent.CompletableFuture;

public interface UserLockUseCase {
  CompletableFuture<UserProjection> lockUser(String username);
}
