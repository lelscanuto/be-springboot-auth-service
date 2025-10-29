package be.school.portal.auth_service.role.application.use_cases;

import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;

public interface RoleRemovePermissionUseCase {
  Role removePermission(@Nonnull Long roleId, @Nonnull Long permissionId);
}
