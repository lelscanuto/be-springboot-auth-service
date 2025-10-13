package be.school.portal.auth_service.application.use_cases;

import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.domain.projections.UserProjection;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface UserTokenExtractUseCase {
  CompletableFuture<UserProjection> extractDetails(@Nonnull TokenRequest tokenRequest);
}
