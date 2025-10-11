package be.school.portal.auth_service.infrastructure.api;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthenticationController {
   
    CompletableFuture<LoginResponse> login(@RequestBody LoginRequest loginRequest);
}
