package be.school.portal.auth_service.domain.entities;

import be.school.portal.auth_service.domain.enums.UserStatus;
import jakarta.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;

@Entity
@Table(
    name = "user_account",
    indexes = @Index(name = "idx_user_username", unique = true, columnList = "username"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAccount {

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

  public Set<Permission> getAllPermissions() {
    return roles.stream()
        .flatMap(role -> role.getPermissions().stream())
        .collect(Collectors.toSet());
  }

  public Set<String> getRoleNames() {
    return roles.stream().map(Role::getName).collect(Collectors.toSet());
  }

  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
  }
}
