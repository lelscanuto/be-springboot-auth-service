package be.school.portal.auth_service.application.services;

import be.school.portal.auth_service.domain.enums.LoginAction;
import jakarta.validation.constraints.NotBlank;

public interface LoginAttemptService {

  void recordFailure(@NotBlank String username, LoginAction loginAction);

  void recordSuccess(@NotBlank String username, LoginAction loginAction);
}
