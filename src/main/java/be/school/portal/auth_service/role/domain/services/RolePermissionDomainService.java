package be.school.portal.auth_service.role.domain.services;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;

public interface RolePermissionDomainService {
  void addPermission(@Nonnull Role existingRole, @Nonnull Permission existingPermission);

  void removePermission(@Nonnull Role existingRole, @Nonnull Permission existingPermission);
}
