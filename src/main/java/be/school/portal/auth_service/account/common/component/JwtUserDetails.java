package be.school.portal.auth_service.account.common.component;

import jakarta.annotation.Nonnull;
import java.util.Set;

public record JwtUserDetails(Long userId, String username, Set<String> roles) {

  public static JwtUserDetails withUserIdAndUsername(
      @Nonnull Long userId, @Nonnull String username) {
    return new JwtUserDetails(userId, username, Set.of());
  }
}
