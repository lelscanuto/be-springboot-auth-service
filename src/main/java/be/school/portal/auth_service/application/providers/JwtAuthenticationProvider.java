package be.school.portal.auth_service.application.providers;

import be.school.portal.auth_service.application.services.CustomUserDetailsService;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.domain.entities.UserAccount;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import lombok.Getter;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public JwtAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    // Fetch user from DB
    UserAccount existingUser =
        userRepository
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
