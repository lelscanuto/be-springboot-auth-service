package be.school.portal.auth_service.role.presentation.api;

import be.school.portal.auth_service.common.dto.RolePermissionResponse;
import be.school.portal.auth_service.common.dto.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Role Lookup", description = "Endpoints for querying roles and their details")
public interface RoleLookUpController {

  @Operation(
      security = {@SecurityRequirement(name = "bearerAuth")},
      summary = "Find all roles by filter",
      description =
          "Fetches a paginated list of roles filtered by name and deletion status. "
              + "If no name is provided, all roles are returned depending on the `isDeleted` flag.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved paginated list of roles",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RoleResponse.class)))
  CompletableFuture<Page<RoleResponse>> findAllByFilter(
      @Parameter(description = "Filter by role name (optional)", example = "Admin") String name,
      @Parameter(description = "Whether to include deleted roles", example = "false")
          Boolean isDeleted,
      @ParameterObject @Parameter(description = "Pagination and sorting information")
          Pageable page);

  @Operation(
      security = {@SecurityRequirement(name = "bearerAuth")},
      summary = "Find role by ID",
      description = "Retrieves a specific role and its details using its unique identifier.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved the role details",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RoleResponse.class)))
  @ApiResponse(responseCode = "404", description = "Role not found")
  CompletableFuture<RoleResponse> findById(
      @Parameter(description = "The unique identifier of the role", example = "1") Long roleId);

  @Operation(
      security = {@SecurityRequirement(name = "bearerAuth")},
      summary = "Find permissions for a role",
      description = "Retrieves all permissions assigned to a given role in a paginated manner.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved paginated role permissions",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RolePermissionResponse.class)))
  @ApiResponse(responseCode = "404", description = "Role not found")
  CompletableFuture<RolePermissionResponse> findRolePermissionByRoleId(
      @Parameter(description = "The unique identifier of the role", example = "1") Long roleId,
      @ParameterObject @Parameter(description = "Pagination and sorting information")
          Pageable page);
}
