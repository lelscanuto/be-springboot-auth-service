package be.school.portal.auth_service.common.utils;

import be.school.portal.auth_service.application.services.CustomUserDetailsService;
import be.school.portal.auth_service.common.exceptions.UnauthorizedException;
import be.school.portal.auth_service.domain.entities.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class that provides convenient access to the currently authenticated user's information
 * from the Spring Security {@link SecurityContextHolder}.
 *
 * <p>This class exposes helper methods to retrieve the authenticated user's username or the full
 * {@link UserAccount} domain object from a {@link CustomUserDetailsService.UserPrincipal}
 * principal.
 *
 * <p>If there is no authenticated user in the current context, methods in this class will throw an
 * {@link UnauthorizedException}.
 *
 * <p>This class is marked as {@code final} and has a private constructor to prevent instantiation.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * UserAccount currentUser = SecurityContextUtil.getUserAccount();
 * String username = SecurityContextUtil.getUsername();
 * }</pre>
 */
public final class SecurityContextUtil {

  /** Private constructor to prevent instantiation. */
  private SecurityContextUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Retrieves the username of the currently authenticated user.
   *
   * @return the authenticated user's username
   * @throws UnauthorizedException if the user is not authenticated or the principal is invalid
   */
  public static String getUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || !(authentication.getPrincipal()
            instanceof CustomUserDetailsService.UserPrincipal userDetails)) {
      throw UnauthorizedException.ofMissingAuth();
    }

    return userDetails.getUsername();
  }
}
