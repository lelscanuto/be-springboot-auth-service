package be.school.portal.auth_service.role.application.adapters;

import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.role.application.mappers.RoleResponseMapper;
import be.school.portal.auth_service.role.application.use_cases.RoleAddPermissionUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleRemovePermissionUseCase;
import be.school.portal.auth_service.role.presentation.facade.RolePermissionFacade;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

/**
 * Adapter implementation of {@link RolePermissionFacade} that acts as a bridge between the
 * application layer use cases and the presentation layer.
 *
 * <p>This adapter orchestrates calls to {@link RoleAddPermissionUseCase} and {@link
 * RoleRemovePermissionUseCase} and maps the resulting {@link
 * be.school.portal.auth_service.role.domain.entities.Role} entities into {@link RoleResponse} DTOs
 * for external consumption.
 */
@Service
public class RolePermissionAdapter implements RolePermissionFacade {

  private final RoleAddPermissionUseCase roleAddPermissionUseCase;
  private final RoleRemovePermissionUseCase roleRemovePermissionUseCase;
  private final RoleResponseMapper roleResponseMapper;

  /**
   * Constructs a new {@link RolePermissionAdapter}.
   *
   * @param roleAddPermissionUseCase use case for adding permissions to roles
   * @param roleRemovePermissionUseCase use case for removing permissions from roles
   * @param roleResponseMapper mapper for converting role entities to response DTOs
   */
  public RolePermissionAdapter(
      RoleAddPermissionUseCase roleAddPermissionUseCase,
      RoleRemovePermissionUseCase roleRemovePermissionUseCase,
      RoleResponseMapper roleResponseMapper) {
    this.roleAddPermissionUseCase = roleAddPermissionUseCase;
    this.roleRemovePermissionUseCase = roleRemovePermissionUseCase;
    this.roleResponseMapper = roleResponseMapper;
  }

  /**
   * Adds a permission to the specified role and returns the updated role as a DTO.
   *
   * @param roleId the ID of the role; must not be null
   * @param permissionId the ID of the permission to add; must not be null
   * @return a {@link RoleResponse} representing the updated role
   */
  @Override
  public RoleResponse addPermission(@Nonnull Long roleId, @Nonnull Long permissionId) {
    return roleResponseMapper.toRoleResponse(
        roleAddPermissionUseCase.addPermission(roleId, permissionId));
  }

  /**
   * Removes a permission from the specified role and returns the updated role as a DTO.
   *
   * @param roleId the ID of the role; must not be null
   * @param permissionId the ID of the permission to remove; must not be null
   * @return a {@link RoleResponse} representing the updated role
   */
  @Override
  public RoleResponse removePermission(@Nonnull Long roleId, @Nonnull Long permissionId) {
    return roleResponseMapper.toRoleResponse(
        roleRemovePermissionUseCase.removePermission(roleId, permissionId));
  }
}
