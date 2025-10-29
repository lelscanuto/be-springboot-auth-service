package be.school.portal.auth_service.role.presentation.facade;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import jakarta.annotation.Nonnull;

public interface RoleManagementFacade {
  RoleResponse createRole(@Nonnull CreateRoleRequest createRoleRequest);

  RoleResponse updateRole(@Nonnull Long id, UpdateRoleRequest updateRoleRequest);

  RoleResponse deleteRole(@Nonnull Long roleId);
}
