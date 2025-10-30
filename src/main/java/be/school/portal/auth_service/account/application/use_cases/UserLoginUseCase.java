package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.application.dto.UserLoginDTO;
import be.school.portal.auth_service.common.dto.LoginRequest;
import jakarta.annotation.Nonnull;

public interface UserLoginUseCase {

  UserLoginDTO login(@Nonnull LoginRequest loginRequest);
}
