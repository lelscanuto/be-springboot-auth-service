package be.school.portal.auth_service.application.use_case;

import be.school.portal.auth_service.domain.projections.UserLiteDTO;
import java.util.concurrent.CompletableFuture;

public interface UserLockUseCase {
  CompletableFuture<UserLiteDTO> lockUser(String username);
}
