package be.school.portal.auth_service.role.application.use_cases;

import be.school.portal.auth_service.role.domain.entities.Role;

public interface RoleAddPermissionUseCase {
  Role addPermission(Long roleId, Long permissionId);
}
