package be.school.portal.auth_service.common.component;

import java.util.Set;

public record JwtUserDetails(String username, Set<String> roles) {

  public static JwtUserDetails withUsername(String username) {
    return new JwtUserDetails(username, Set.of());
  }
}
