package be.school.portal.auth_service.account.common.builders;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public final class SecurityExceptionFactory {

  private SecurityExceptionFactory() {} // utility class

  public static AuthenticationCredentialsNotFoundException missingAuth() {
    return new AuthenticationCredentialsNotFoundException("User is not authenticated.");
  }

  public static class UsernameNotFoundExceptionFactory {

    private UsernameNotFoundExceptionFactory() {}

    public static UsernameNotFoundException ofUsername(String username) {
      return new UsernameNotFoundException("User not found: " + username);
    }
  }

  public static class AccessDeniedExceptionFactory {

    private AccessDeniedExceptionFactory() {}

    public static AccessDeniedException forUserMismatch(String username) {
      return new AccessDeniedException(
          "User mismatch: " + username + " is not authorized for this resource.");
    }
  }

  public static class BadCredentialsExceptionFactory {

    private BadCredentialsExceptionFactory() {}

    public static BadCredentialsException forUsername(String username) {
      return new BadCredentialsException("Invalid credentials for user: " + username);
    }

    public static BadCredentialsException forToken(String token) {
      return new BadCredentialsException("Invalid or expired token: " + token);
    }

    public static BadCredentialsException ofTokenType(String tokenType) {
      return new BadCredentialsException(
          "Token is not a refresh token, but a [" + tokenType + "] token.");
    }
  }

  public static class UserStateExceptionFactory {

    private UserStateExceptionFactory() {}

    public static DisabledException disabled(String username) {
      return new DisabledException("User is disabled: " + username);
    }

    public static LockedException locked(String username) {
      return new LockedException("User account is locked: " + username);
    }
  }
}
