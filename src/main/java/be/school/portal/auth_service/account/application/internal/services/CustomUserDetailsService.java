package be.school.portal.auth_service.account.application.internal.services;

import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.role.domain.entities.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepositoryPort userRepositoryPort;

  public CustomUserDetailsService(UserRepositoryPort userRepositoryPort) {
    this.userRepositoryPort = userRepositoryPort;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserAccount user =
        userRepositoryPort
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return new UserPrincipal(user);
  }

  public static class UserPrincipal implements UserDetails {

    private final String username;
    private final Set<Role> roles;
    private final boolean active;

    public UserPrincipal(UserAccount user) {
      this.username = user.getUsername();
      this.roles = Optional.ofNullable(user.getRoles()).orElse(Set.of());
      this.active = user.isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      Set<GrantedAuthority> authorities = new HashSet<>();

      // Add roles
      roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

      // Add permissions from roles
      roles.stream()
          .map(Role::getPermissions) // Stream<Set<Permission>>
          .flatMap(Set::stream) // flatten to Stream<Permission>
          .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

      return authorities;
    }

    @Override
    public String getPassword() {
      return null;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return active;
    }
  }
}
