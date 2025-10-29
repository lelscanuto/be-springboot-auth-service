package be.school.portal.auth_service.role.domain.entities;

import be.school.portal.auth_service.common.entities.AuditableEntity;
import be.school.portal.auth_service.common.entities.SoftDeletable;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "role", indexes = @Index(name = "idx_role_name", unique = true, columnList = "name"))
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Role extends AuditableEntity implements SoftDeletable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "is_deleted", nullable = false)
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private Boolean isDeleted = false;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "role_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private Set<Permission> permissions;

  private Role(String name) {
    this.name = name;
  }

  public static Role withName(@NotBlank String name) {
    return new Role(name);
  }

  public void addPermission(Permission existingPermission) {
    permissions.add(existingPermission);
  }

  public void removePermission(Permission existingPermission) {
    permissions.remove(existingPermission);
  }

  @Override
  public boolean isDeleted() {
    return this.isDeleted;
  }

  @Override
  public void delete() {
    this.isDeleted = true;
  }

  @Override
  public void revoke() {
    this.isDeleted = false;
  }
}
