package be.school.portal.auth_service.application.services;

import be.school.portal.auth_service.domain.entities.Role;
import be.school.portal.auth_service.domain.entities.UserAccount;
import be.school.portal.auth_service.infrastructure.repositories.UserRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserAccount user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return new CustomUserDetails(user);
  }

  public static class CustomUserDetails implements UserDetails {

    private final UserAccount user;

    public CustomUserDetails(UserAccount user) {
      this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      Set<GrantedAuthority> authorities = new HashSet<>();

      // Add roles
      user.getRoles()
          .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

      // Add permissions from roles
      user.getRoles().stream()
          .map(Role::getPermissions) // Stream<Set<Permission>>
          .flatMap(Set::stream) // flatten to Stream<Permission>
          .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

      return authorities;
    }

    @Override
    public String getPassword() {
      return user.getPassword();
    }

    @Override
    public String getUsername() {
      return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return user.isActive();
    }
  }
}
