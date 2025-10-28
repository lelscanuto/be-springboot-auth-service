package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.application.dto.LoginRequest;
import be.school.portal.auth_service.account.application.dto.LoginResponse;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface UserLoginUseCase {

  CompletableFuture<LoginResponse> login(@Nonnull LoginRequest loginRequest);
}
