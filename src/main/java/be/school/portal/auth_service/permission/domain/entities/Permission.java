package be.school.portal.auth_service.permission.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "permission",
    indexes = @Index(name = "idx_permission_name", unique = true, columnList = "name"))
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
public class Permission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @EqualsAndHashCode.Include
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  public static Permission withName(String permissionName) {
    Permission permission = new Permission();
    permission.setName(permissionName);
    return permission;
  }
}
