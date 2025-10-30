package be.school.portal.auth_service.role.presentation.facade;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface RoleManagementFacade {
  CompletableFuture<RoleResponse> createRole(@Nonnull CreateRoleRequest createRoleRequest);

  CompletableFuture<RoleResponse> updateRole(@Nonnull Long id, UpdateRoleRequest updateRoleRequest);

  CompletableFuture<RoleResponse> deleteRole(@Nonnull Long roleId);
}
