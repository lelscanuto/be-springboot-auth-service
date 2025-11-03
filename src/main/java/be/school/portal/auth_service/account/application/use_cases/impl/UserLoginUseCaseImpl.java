package be.school.portal.auth_service.account.application.use_cases.impl;

import be.school.portal.auth_service.account.application.aspects.TrackLogin;
import be.school.portal.auth_service.account.application.dto.UserLoginDTO;
import be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService;
import be.school.portal.auth_service.account.application.use_cases.UserLoginUseCase;
import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import be.school.portal.auth_service.common.dto.LoginRequest;
import be.school.portal.auth_service.common.security.UserPrincipal;
import jakarta.annotation.Nonnull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service (use case) responsible for authenticating users and initiating secure login
 * sessions.
 *
 * <p>This implementation is part of the <strong>application layer</strong> in a DDD architecture.
 * It coordinates the authentication process, token generation, and user validation without
 * containing domain-specific business logic itself.
 *
 * <p>The use case delegates:
 *
 * <ul>
 *   <li>Authentication to Spring Security’s {@link AuthenticationManager}
 *   <li>Token generation to {@link
 *       be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService}
 *   <li>User retrieval and status validation to {@link
 *       be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase}
 * </ul>
 *
 * tra
 *
 * <h3>Asynchronous Execution</h3>
 *
 * <p>Although the class itself is not annotated with {@link
 * org.springframework.scheduling.annotation.Async}, it is typically invoked through asynchronous
 * adapters (e.g., API or event-driven layers) for improved scalability and responsiveness.
 *
 * <h3>Security Considerations</h3>
 *
 * <ul>
 *   <li>Credentials are verified via {@link AuthenticationManager}, ensuring password hashing
 *       alignment with Spring Security.
 *   <li>Disabled or locked accounts are rejected prior to token issuance.
 *   <li>JWT token creation is delegated to a specialized component for consistent signing and
 *       claims management.
 * </ul>
 *
 * <h3>Auditing & Observability</h3>
 *
 * <p>The method is annotated with {@link
 * be.school.portal.auth_service.account.application.aspects.TrackLogin}, which automatically logs
 * success or failure events via AOP-based interceptors (see {@code LoginTrackerAspect}). This
 * supports compliance reporting and intrusion detection workflows.
 *
 * <h3>Example Usage</h3>
 *
 * <pre>{@code
 * LoginRequest request = new LoginRequest("john.doe", "password123");
 * UserLoginDTO loginResult = userLoginUseCase.login(request);
 *
 * System.out.println("Access token: " + loginResult.token().accessToken());
 * }</pre>
 *
 * @see be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService
 * @see be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase
 * @see org.springframework.security.authentication.AuthenticationManager
 * @author Francis Jorell J. Canuto
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserLoginUseCaseImpl implements UserLoginUseCase {

  private final AuthenticationManager authenticationManager;
  private final UserTokenCreationService userTokenCreationService;
  private final UserLookUpUseCase userLookUpUseCase;

  /**
   * Constructs a new instance of {@link UserLoginUseCaseImpl} with the required dependencies.
   *
   * @param authenticationManager the Spring Security component responsible for verifying
   *     credentials
   * @param userTokenCreationService the service responsible for generating signed JWT tokens
   * @param userLookUpUseCase the use case responsible for retrieving user information from
   *     persistent storage
   */
  public UserLoginUseCaseImpl(
      AuthenticationManager authenticationManager,
      UserTokenCreationService userTokenCreationService,
      UserLookUpUseCase userLookUpUseCase) {
    this.authenticationManager = authenticationManager;
    this.userTokenCreationService = userTokenCreationService;
    this.userLookUpUseCase = userLookUpUseCase;
  }

  /**
   * Authenticates a user based on provided credentials and issues a JWT token upon success.
   *
   * <p>Operational steps:
   *
   * <ol>
   *   <li>Authenticate credentials using {@link AuthenticationManager}
   *   <li>Retrieve and validate the corresponding user account
   *   <li>Generate and return a signed JWT access token
   * </ol>
   *
   * <p>The method is integrated with {@link
   * be.school.portal.auth_service.account.application.aspects.TrackLogin} for login attempt
   * auditing and anomaly tracking.
   *
   * @param loginRequest the incoming authentication request containing username and password
   * @return a {@link UserLoginDTO} encapsulating the authenticated user and issued JWT token
   * @throws org.springframework.security.authentication.BadCredentialsException if authentication
   *     fails due to invalid username or password
   * @throws org.springframework.security.authentication.DisabledException if the user account is
   *     disabled
   * @throws org.springframework.security.authentication.LockedException if the user account is
   *     locked
   */
  @Override
  @TrackLogin
  public UserLoginDTO login(@Nonnull LoginRequest loginRequest) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password()));

    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    // Retrieve user domain object
    final var authenticatedUser = userLookUpUseCase.findByUsername(userPrincipal.getUsername());

    // Generate signed JWT token
    final var token = userTokenCreationService.create(authenticatedUser);

    // Return a DTO encapsulating user and token details
    return UserLoginDTO.ofAccountAndToken(authenticatedUser, token);
  }
}
