package be.school.portal.auth_service.common.security;

import be.school.portal.auth_service.account.application.internal.services.AuthenticationService;
import be.school.portal.auth_service.account.domain.exception.UserNotFoundException;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation responsible for authenticating users based on username and password
 * credentials within a JWT-enabled security architecture.
 *
 * <p>This class integrates with Spring Security’s authentication framework by implementing the
 * {@link AuthenticationService} contract. It validates user credentials, checks account status, and
 * returns a fully authenticated {@link Authentication} token containing user authorities.
 *
 * <p>Typical usage flow:
 *
 * <ol>
 *   <li>The client submits a login request with username and password.
 *   <li>This service retrieves the corresponding {@link UserDetails} via {@link
 *       UserDetailsService}.
 *   <li>The provided password is verified against the stored, encoded password using {@link
 *       PasswordEncoder}.
 *   <li>If validation succeeds, a {@link UsernamePasswordAuthenticationToken} is returned with the
 *       authenticated {@link UserPrincipal} and granted authorities.
 * </ol>
 *
 * <p>Authentication failures are reported through {@link AuthenticationException} and its
 * subclasses, such as {@link BadCredentialsException}.
 *
 * @see org.springframework.security.authentication.AuthenticationManager
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see org.springframework.security.crypto.password.PasswordEncoder
 * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 * @author Francis Jorell Canuto
 */
@Service
public class JwtAuthenticationService implements AuthenticationService {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a new {@code JwtAuthenticationService} instance.
   *
   * @param userDetailsService the service used to retrieve user data from persistence or cache
   * @param passwordEncoder the encoder used to verify raw passwords against stored password hashes
   */
  public JwtAuthenticationService(
      UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Authenticates a user based on their provided credentials.
   *
   * <p>This method performs the following steps:
   *
   * <ul>
   *   <li>Loads the user’s details from the {@link UserDetailsService}.
   *   <li>Validates the provided password against the stored hash.
   *   <li>Verifies that the user’s account is enabled.
   *   <li>Returns a fully authenticated {@link UsernamePasswordAuthenticationToken} containing the
   *       user’s authorities.
   * </ul>
   *
   * @param authentication the {@link Authentication} object containing the username and password
   *     credentials submitted by the client
   * @return a fully authenticated {@link UsernamePasswordAuthenticationToken} if validation
   *     succeeds
   * @throws BadCredentialsException if the username or password is invalid
   * @throws AuthenticationException if authentication fails due to account state or other security
   *     issues
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    // Retrieve user details
    UserDetails userDetails;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (UserNotFoundException userNotFoundException) {
      throw new BadCredentialsException("Invalid username or password", userNotFoundException);
    }

    // Validate credentials
    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw SecurityExceptionFactory.BadCredentialsExceptionFactory.forUsername(
          userDetails.getUsername());
    }

    // Verify user state
    if (!userDetails.isEnabled()) {
      throw SecurityExceptionFactory.UserStateExceptionFactory.disabled(userDetails.getUsername());
    }

    // Create principal context and return authenticated token
    final var userPrincipalContext = new UserPrincipal(userDetails);
    return new UsernamePasswordAuthenticationToken(
        userPrincipalContext, null, userPrincipalContext.getAuthorities());
  }

  /**
   * Determines whether this authentication provider supports the given authentication type.
   *
   * <p>This implementation supports authentication requests using {@link
   * UsernamePasswordAuthenticationToken}.
   *
   * @param authentication the class of the authentication request object
   * @return {@code true} if the given authentication type is supported, {@code false} otherwise
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
