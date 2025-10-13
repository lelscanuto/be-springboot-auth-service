package be.school.portal.auth_service.application.use_cases.impl;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.application.dto.TokenRequest;
import be.school.portal.auth_service.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.application.services.UserTokenCreationService;
import be.school.portal.auth_service.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.common.component.JwtTokenComponent;
import be.school.portal.auth_service.common.exceptions.InvalidCredentialException;
import be.school.portal.auth_service.common.exceptions.UserInvalidStateException;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
@Validated
public class UserTokenRefreshUseCaseImpl implements UserTokenRefreshUseCase {

  private final UserRepository userRepository;
  private final UserTokenCreationService userTokenCreationService;
  private final LoginResponseMapper loginResponseMapper;
  private final JwtTokenComponent jwtTokenComponent;

  public UserTokenRefreshUseCaseImpl(
      UserRepository userRepository,
      UserTokenCreationService userTokenCreationService,
      LoginResponseMapper loginResponseMapper,
      JwtTokenComponent jwtTokenComponent) {
    this.userRepository = userRepository;
    this.userTokenCreationService = userTokenCreationService;
    this.loginResponseMapper = loginResponseMapper;
    this.jwtTokenComponent = jwtTokenComponent;
  }

  @Override
  @Async
  public CompletableFuture<LoginResponse> refresh(@Valid @Nonnull TokenRequest tokenRequest) {

    // Extract the refresh token from the request
    final var refreshToken = tokenRequest.token();

    // 1️⃣ Validate refresh token
    if (!jwtTokenComponent.validateToken(refreshToken)) {
      throw InvalidCredentialException.ofToken(refreshToken);
    }

    // 2️⃣ Ensure it’s a refresh token (not access)
    JwtTokenComponent.TokenType tokenType = jwtTokenComponent.getTokenType(refreshToken);
    if (JwtTokenComponent.TokenType.REFRESH != tokenType) {
      throw InvalidCredentialException.ofTokenType(tokenType.name());
    }

    // 3️⃣ Extract username and find the user
    final var username = jwtTokenComponent.getUsernameFromToken(refreshToken);

    final var existingUser =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

    if (!existingUser.isActive()) {
      throw UserInvalidStateException.ofUsernameAndStatus(
          existingUser.getUsername(), existingUser.getStatus());
    }

    // 4️⃣ Generate new tokens
    final var userToken = userTokenCreationService.create(existingUser);

    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(existingUser, userToken));
  }
}
