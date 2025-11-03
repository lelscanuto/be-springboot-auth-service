package be.school.portal.auth_service.common.security;

import be.school.portal.auth_service.account.application.port.UserCachingPort;
import be.school.portal.auth_service.account.domain.exception.UserNotFoundException;
import be.school.portal.auth_service.account.domain.projections.UserProjection;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} responsible for retrieving user
 * authentication and authorization details for Spring Security.
 *
 * <p>This service acts as a bridge between the application’s domain model and Spring Security’s
 * authentication framework. It loads user information (e.g., username, password, roles, and
 * permissions) from a cache or persistent data source through the {@link UserCachingPort} and
 * adapts it into a {@link CustomUserDetails} instance compatible with Spring Security.
 *
 * <p>The loaded {@code UserDetails} is leveraged by authentication mechanisms such as:
 *
 * <ul>
 *   <li>Form-based or API-based username/password authentication
 *   <li>Token-based authentication (e.g., JWT validation in filters)
 * </ul>
 *
 * <p>If the specified user cannot be found, a {@link UsernameNotFoundException} is thrown to signal
 * an authentication failure event.
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see org.springframework.security.authentication.AuthenticationManager
 * @see CustomUserDetails
 * @see UserCachingPort
 * @author Francis Jorell Canuto
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserCachingPort userCachingPort;

  /**
   * Constructs a new {@code CustomUserDetailsService} with the specified caching port.
   *
   * @param userCachingPort abstraction for accessing user data, providing lookup capabilities
   *     either from cache (e.g., Redis) or a fallback data source (e.g., database)
   */
  public CustomUserDetailsService(UserCachingPort userCachingPort) {
    this.userCachingPort = userCachingPort;
  }

  /**
   * Loads user-specific data by username.
   *
   * <p>This method is automatically invoked by Spring Security during the authentication process.
   * It attempts to retrieve a {@link UserProjection} from the configured cache or data source, and
   * transforms it into a {@link CustomUserDetails} instance recognized by Spring Security’s
   * authentication infrastructure.
   *
   * <p>The returned object encapsulates credentials, roles, and granted authorities, which are
   * subsequently used to verify access permissions throughout the application.
   *
   * @param username the unique identifier of the user to retrieve
   * @return a fully initialized {@link CustomUserDetails} containing user credentials and
   *     authorities
   * @throws UsernameNotFoundException if no user with the given username exists or cannot be loaded
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserProjection userProjection;

    try {
      // Attempt to retrieve the user projection from the caching port
      userProjection = userCachingPort.findByUsername(username);
    } catch (UserNotFoundException ex) {
      // If the user is not found, throw a UsernameNotFoundException
      throw SecurityExceptionFactory.UsernameNotFoundExceptionFactory.ofUsername(username);
    }

    // return a CustomUserDetails
    return new CustomUserDetails(userProjection);
  }
}
