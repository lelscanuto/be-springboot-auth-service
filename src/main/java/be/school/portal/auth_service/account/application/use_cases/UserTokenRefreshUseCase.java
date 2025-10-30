package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.application.dto.UserLoginDTO;
import be.school.portal.auth_service.common.dto.TokenRequest;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;

public interface UserTokenRefreshUseCase {
  UserLoginDTO  refresh(@Nonnull @Valid TokenRequest tokenRequest);
}
