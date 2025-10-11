package be.school.portal.auth_service.domain.entities;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "role", indexes = @Index(name = "idx_role_name", unique = true, columnList = "name"))
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  @EqualsAndHashCode.Include
  private String name;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "role_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private Set<Permission> permissions;
}
