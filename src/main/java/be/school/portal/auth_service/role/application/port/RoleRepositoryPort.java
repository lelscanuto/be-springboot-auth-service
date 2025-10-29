package be.school.portal.auth_service.role.application.port;

import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;
import java.util.Optional;

public interface RoleRepositoryPort {
  Optional<Role> findById(@Nonnull Long roleId);

  Role save(@Nonnull Role existingRole);

  Boolean existsByName(@Nonnull String name);

  Role delete(@Nonnull Role existingRole);
}
