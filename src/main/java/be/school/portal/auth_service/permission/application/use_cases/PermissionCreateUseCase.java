package be.school.portal.auth_service.permission.application.use_cases;

import be.school.portal.auth_service.common.dto.CreatePermissionRequest;
import be.school.portal.auth_service.permission.domain.entities.Permission;

public interface PermissionCreateUseCase {
  Permission create(CreatePermissionRequest createPermissionRequest);
}
