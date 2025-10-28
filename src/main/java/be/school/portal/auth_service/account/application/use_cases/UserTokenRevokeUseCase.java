package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.application.dto.TokenRequest;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface UserTokenRevokeUseCase {
  CompletableFuture<Void> revoke(@Nonnull TokenRequest tokenRequest);
}
