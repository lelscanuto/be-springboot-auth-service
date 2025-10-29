package be.school.portal.auth_service.role.presentation.api;

import be.school.portal.auth_service.common.dto.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Role Permissions", description = "Manage permissions for roles")
public interface RolePermissionController {

  @Operation(
      summary = "Add permission to a role",
      description = "Assigns a permission to the specified role by their IDs")
  RoleResponse addPermission(
      @Parameter(description = "ID of the role", example = "1") Long roleId,
      @Parameter(description = "ID of the permission", example = "101") Long permissionId);

  @Operation(
      summary = "Remove permission from a role",
      description = "Removes a permission from the specified role by their IDs")
  RoleResponse removePermission(
      @Parameter(description = "ID of the role", example = "1") Long roleId,
      @Parameter(description = "ID of the permission", example = "101") Long permissionId);
}
