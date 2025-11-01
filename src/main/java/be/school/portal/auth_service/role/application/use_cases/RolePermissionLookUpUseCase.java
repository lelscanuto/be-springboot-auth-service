package be.school.portal.auth_service.role.application.use_cases;

import be.school.portal.auth_service.role.application.dto.RolePageablePermissionDTO;
import org.springframework.data.domain.Pageable;

public interface RolePermissionLookUpUseCase {

  boolean isPermissionUsed(Long permissionId);

  RolePageablePermissionDTO findAllByRoleId(Long roleId, Pageable pageable);
}
