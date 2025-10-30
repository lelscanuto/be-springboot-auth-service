package be.school.portal.auth_service.role.presentation.api;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Role Management", description = "APIs for managing roles")
public interface RoleManagementController {

  @Operation(
      security = {@SecurityRequirement(name = "bearerAuth")},
      summary = "Create a new role",
      description = "Creates a role with the specified details.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Role already exists")
      })
  CompletableFuture<RoleResponse> createRole(CreateRoleRequest createRoleRequest);

  @Operation(
      security = {@SecurityRequirement(name = "bearerAuth")},
      summary = "Update an existing role",
      description = "Updates role details by ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Role not found")
      })
  CompletableFuture<RoleResponse> updateRole(Long roleId, UpdateRoleRequest updateRoleRequest);

  @Operation(
      security = {@SecurityRequirement(name = "bearerAuth")},
      summary = "Delete a role",
      description = "Deletes a role by its ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found")
      })
  CompletableFuture<RoleResponse> deleteRole(Long roleId);
}
