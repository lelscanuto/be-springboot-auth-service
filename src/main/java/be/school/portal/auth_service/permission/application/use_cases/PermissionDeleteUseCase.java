package be.school.portal.auth_service.permission.application.use_cases;

import be.school.portal.auth_service.permission.domain.entities.Permission;

public interface PermissionDeleteUseCase {
  Permission delete(Long permissionId);
}
