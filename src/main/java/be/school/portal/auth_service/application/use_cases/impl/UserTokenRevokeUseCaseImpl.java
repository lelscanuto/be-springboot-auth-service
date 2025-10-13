package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.use_cases.UserTokenRevokeUseCase;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class UserTokenRevokeUseCaseImpl implements UserTokenRevokeUseCase {
  @Override
  public CompletableFuture<Void> revoke(@Nonnull TokenRequest tokenRequest) {
    return null;
  }
}
