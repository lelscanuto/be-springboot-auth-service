package be.school.portal.auth_service.common.security;

import be.school.portal.auth_service.account.domain.enums.UserStatus;
import be.school.portal.auth_service.account.domain.projections.UserProjection;
import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  @Serial private static final long serialVersionUID = -6867633511023296774L;

  private final String username;
  private final String password;
  private final UserStatus status;
  private final Set<String> roles;
  private final Set<String> permissions;

  public CustomUserDetails(UserProjection userProjection) {
    this.username = userProjection.username();
    this.password = userProjection.password();
    this.roles = userProjection.roles();
    this.permissions = userProjection.permissions();
    this.status = userProjection.status();
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();

    // Add roles
    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

    // Add permissions
    permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

    return authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.status != UserStatus.LOCKED;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return status == UserStatus.ACTIVE;
  }
}
