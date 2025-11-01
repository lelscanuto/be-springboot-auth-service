package be.school.portal.auth_service.role.application.port;

import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleRepositoryPort {
  Optional<Role> findById(@Nonnull Long roleId);

  Role save(@Nonnull Role existingRole);

  boolean existsByName(@Nonnull String name);

  Role delete(@Nonnull Role existingRole);

  List<Role> findAllByIsDeletedFalseAndPermissionsId(@Nonnull Long permissionId);

  Page<Role> findAll(Specification<Role> and, Pageable pageable);
}
