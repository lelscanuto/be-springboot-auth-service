package be.school.portal.auth_service.account.domain.entities;

import be.school.portal.auth_service.account.domain.enums.UserStatus;
import be.school.portal.auth_service.common.builders.SecurityExceptionFactory;
import be.school.portal.auth_service.common.entities.AuditableEntity;
import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;

@Entity
@Table(
    name = "user_account",
    indexes = @Index(name = "idx_user_username", unique = true, columnList = "username"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserAccount extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @Setter(AccessLevel.NONE)
  private Long id;

  @EqualsAndHashCode.Include
  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "status", nullable = false)
  private UserStatus status;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Setter(AccessLevel.NONE)
  private Set<RefreshToken> refreshTokens = new HashSet<>();

  public void addRefreshToken(RefreshToken refreshToken) {
    this.refreshTokens.add(refreshToken);
    refreshToken.setUser(this);
  }

  public void removeRefreshToken(RefreshToken refreshToken) {
    this.refreshTokens.remove(refreshToken);
    refreshToken.setUser(null);
  }

  public Set<String> getRoleNames() {
    return roles.stream().map(Role::getName).collect(Collectors.toSet());
  }

  public RefreshToken getRefreshTokensWithId(long refreshTokenId) {
    return this.refreshTokens.stream()
        .filter(rt -> rt.getId() == refreshTokenId && rt.isActive())
        .findFirst()
        .orElseThrow(() -> new AccessDeniedException("Invalid refresh token"));
  }

  public void revokeTokenWith(long refreshTokenId) {
    final var refreshToken = getRefreshTokensWithId(refreshTokenId);

    refreshToken.revoke();
  }

  public void ensureActive() {
    // Check if user is inactive
    if (UserStatus.INACTIVE == this.status) {
      throw SecurityExceptionFactory.UserStateExceptionFactory.disabled(this.getUsername());
    }

    // Check if user is lock
    if (UserStatus.LOCKED == this.status) {
      throw SecurityExceptionFactory.UserStateExceptionFactory.locked(this.getUsername());
    }
  }
}
