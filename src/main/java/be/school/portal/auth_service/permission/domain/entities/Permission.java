package be.school.portal.auth_service.permission.domain.entities;

import be.school.portal.auth_service.common.entities.AuditableEntity;
import be.school.portal.auth_service.common.entities.SoftDeletable;
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
public class Permission extends AuditableEntity implements SoftDeletable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @EqualsAndHashCode.Include
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

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
    this.isDeleted = true;
  }
}
