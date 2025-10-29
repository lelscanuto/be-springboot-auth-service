package be.school.portal.auth_service.role.presentation.facade;

import be.school.portal.auth_service.common.dto.RoleResponse;
import jakarta.annotation.Nonnull;

public interface RolePermissionFacade {
  RoleResponse addPermission(@Nonnull Long roleId, @Nonnull Long permissionId);

  RoleResponse removePermission(@Nonnull Long roleId, @Nonnull Long permissionId);
}
