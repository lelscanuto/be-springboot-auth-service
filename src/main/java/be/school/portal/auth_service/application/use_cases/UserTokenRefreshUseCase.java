package be.school.portal.auth_service.application.use_cases;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

public interface UserTokenRefreshUseCase {
  CompletableFuture<LoginResponse> refresh(@Nonnull @Valid TokenRequest tokenRequest);
}
