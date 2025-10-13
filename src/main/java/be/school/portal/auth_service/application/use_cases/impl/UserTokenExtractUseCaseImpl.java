package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.use_cases.UserTokenExtractUseCase;
import be.school.portal.auth_service.domain.projections.UserProjection;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class UserTokenExtractUseCaseImpl implements UserTokenExtractUseCase {
  @Override
  public CompletableFuture<UserProjection> extractDetails(@Nonnull TokenRequest tokenRequest) {
    return null;
  }
}
