package be.school.portal.auth_service.account.application.adapters;

import be.school.portal.auth_service.account.application.mappers.LoginResponseMapper;
import be.school.portal.auth_service.account.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserLogoutUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserTokenRefreshUseCase;
import be.school.portal.auth_service.account.presentation.facade.UserAuthenticationFacade;
import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.dto.LoginRequest;
import be.school.portal.auth_service.common.dto.LoginResponse;
import be.school.portal.auth_service.common.dto.TokenRequest;
import be.school.portal.auth_service.common.logging.LoggerName;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Asynchronous implementation of the {@link UserAuthenticationFacade} interface responsible for
 * handling user authentication-related API operations, including login, logout, and token refresh.
 *
 * <p>This adapter delegates core authentication logic to the domain-level use cases:
 *
 * <ul>
 *   <li>{@link UserLoginUseCase} – handles user credential verification and token issuance
 *   <li>{@link UserLogoutUseCase} – manages token revocation and user session termination
 *   <li>{@link UserTokenRefreshUseCase} – generates new access tokens using valid refresh tokens
 * </ul>
 *
 * <p>All operations are executed asynchronously via Spring's {@link
 * org.springframework.scheduling.annotation.Async} support, returning {@link
 * java.util.concurrent.CompletableFuture} instances to enable non-blocking execution.
 *
 * <p>The {@link Trace} annotation ensures method-level tracing of input arguments and results for
 * enhanced observability, diagnostics, and auditability.
 *
 * @author Francis Jorell Canuto
 */
@Service
@Trace(logger = LoggerName.API_LOGGER)
public class AsyncUserAuthenticationApiAdapter implements UserAuthenticationFacade {

  private final UserLoginUseCase userLoginUseCase;
  private final UserTokenRefreshUseCase userTokenRefreshUseCase;
  private final UserLogoutUseCase userLogoutUseCase;
  private final LoginResponseMapper loginResponseMapper;

  /**
   * Constructs an instance of {@code AsyncUserAuthenticationApiAdapter} with the required
   * authentication-related use cases and mapping utilities.
   *
   * @param userLoginUseCase the use case responsible for handling user login operations
   * @param userTokenRefreshUseCase the use case responsible for refreshing JWT access tokens
   * @param userLogoutUseCase the use case responsible for executing user logout and token
   *     revocation
   * @param loginResponseMapper the mapper used to convert domain entities into API response DTOs
   */
  public AsyncUserAuthenticationApiAdapter(
      UserLoginUseCase userLoginUseCase,
      UserTokenRefreshUseCase userTokenRefreshUseCase,
      UserLogoutUseCase userLogoutUseCase,
      LoginResponseMapper loginResponseMapper) {
    this.userLoginUseCase = userLoginUseCase;
    this.userTokenRefreshUseCase = userTokenRefreshUseCase;
    this.userLogoutUseCase = userLogoutUseCase;
    this.loginResponseMapper = loginResponseMapper;
  }

  /**
   * Executes the user login process asynchronously.
   *
   * <p>This method authenticates the provided user credentials, generates access and refresh
   * tokens, and maps the result into a {@link LoginResponse} DTO.
   *
   * @param loginRequest the request containing the user’s login credentials
   * @return a {@link CompletableFuture} containing the login response with access and refresh
   *     tokens
   */
  @Override
  @Async
  public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {
    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(userLoginUseCase.login(loginRequest)));
  }

  /**
   * Executes the user logout process asynchronously.
   *
   * <p>This operation revokes the user's active session or tokens and completes the future without
   * a return value.
   *
   * @param tokenRequest the request containing the token to be revoked
   * @return a {@link CompletableFuture} representing the completion of the logout operation
   */
  @Override
  @Async
  public CompletableFuture<Void> logout(TokenRequest tokenRequest) {
    userLogoutUseCase.logout(tokenRequest);
    return CompletableFuture.completedFuture(null);
  }

  /**
   * Executes the token refresh process asynchronously.
   *
   * <p>This method validates the refresh token, issues a new access token, and maps the response
   * into a {@link LoginResponse} DTO.
   *
   * @param tokenRequest the request containing the refresh token
   * @return a {@link CompletableFuture} containing the response with refreshed authentication
   *     tokens
   */
  @Override
  @Async
  public CompletableFuture<LoginResponse> refreshToken(TokenRequest tokenRequest) {
    return CompletableFuture.completedFuture(
        loginResponseMapper.toLoginResponse(userTokenRefreshUseCase.refresh(tokenRequest)));
  }
}
