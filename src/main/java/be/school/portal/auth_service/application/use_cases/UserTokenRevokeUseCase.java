package be.school.portal.auth_service.application.use_cases;

import be.school.portal.auth_service.application.dto.TokenRequest;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface UserTokenRevokeUseCase {
  CompletableFuture<Void> revoke(@Nonnull TokenRequest tokenRequest);
}
