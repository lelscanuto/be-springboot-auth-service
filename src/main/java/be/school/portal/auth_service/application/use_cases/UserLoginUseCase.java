package be.school.portal.auth_service.application.use_cases;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface UserLoginUseCase {

  CompletableFuture<LoginResponse> login(@Nonnull LoginRequest loginRequest);
}
