package be.school.portal.auth_service.account.application.internal.services.impl;

import be.school.portal.auth_service.account.application.internal.services.AuthenticationService;
import be.school.portal.auth_service.account.application.internal.services.CustomUserDetailsService;
import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import lombok.Getter;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationServiceImpl implements AuthenticationService {

  private final UserRepositoryPort userRepositoryPort;
  private final PasswordEncoder passwordEncoder;

  public JwtAuthenticationServiceImpl(
      UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
    this.userRepositoryPort = userRepositoryPort;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    // Fetch user from DB
    UserAccount existingUser =
        userRepositoryPort
            .findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

    // Verify password
    if (!passwordEncoder.matches(password, existingUser.getPassword())) {
      throw SecurityExceptionFactory.BadCredentialsExceptionFactory.forUsername(
          existingUser.getUsername());
    }

    // Assert user state
    existingUser.ensureActive();

    final var userPrincipalContext = UserPrincipalContext.ofContext(existingUser);

    // 4️⃣ Return authenticated token with authorities
    return new UsernamePasswordAuthenticationToken(
        userPrincipalContext, null, userPrincipalContext.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    // Supports username/password authentication
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Getter
  public static class UserPrincipalContext extends CustomUserDetailsService.UserPrincipal {

    private final UserAccount context;

    private UserPrincipalContext(UserAccount user) {
      super(user);
      this.context = user;
    }

    public static UserPrincipalContext ofContext(UserAccount userAccount) {
      return new UserPrincipalContext(userAccount);
    }
  }
}
