package be.school.portal.auth_service.common.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents the authenticated user without sensitive credentials.
 *
 * <p>This class is used as a safe user principal for JWT tokens or API responses. Unlike {@link
 * CustomUserDetails}, this class excludes the password field.
 */
@Getter
public class UserPrincipal implements Serializable {

  @Serial private static final long serialVersionUID = 1223445234234234L;

  private final String username;
  private final Collection<GrantedAuthority> authorities;

  /**
   * Creates a new {@link UserPrincipal} based on a {@link CustomUserDetails}.
   *
   * @param userDetails the authenticated user details
   */
  public UserPrincipal(UserDetails userDetails) {
    this.username = userDetails.getUsername();
    this.authorities = List.copyOf(userDetails.getAuthorities());
  }
}
