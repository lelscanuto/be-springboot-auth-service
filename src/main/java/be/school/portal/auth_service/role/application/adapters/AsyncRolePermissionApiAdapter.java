package be.school.portal.auth_service.role.application.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.logging.LoggerName;
import be.school.portal.auth_service.role.application.mappers.RoleResponseMapper;
import be.school.portal.auth_service.role.application.use_cases.RoleAddPermissionUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleRemovePermissionUseCase;
import be.school.portal.auth_service.role.presentation.facade.RolePermissionFacade;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
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
@Trace(logger = LoggerName.API_LOGGER)
public class AsyncRolePermissionApiAdapter implements RolePermissionFacade {

  private final RoleAddPermissionUseCase roleAddPermissionUseCase;
  private final RoleRemovePermissionUseCase roleRemovePermissionUseCase;
  private final RoleResponseMapper roleResponseMapper;

  /**
   * Constructs a new {@link AsyncRolePermissionApiAdapter}.
   *
   * @param roleAddPermissionUseCase use case for adding permissions to roles
   * @param roleRemovePermissionUseCase use case for removing permissions from roles
   * @param roleResponseMapper mapper for converting role entities to response DTOs
   */
  public AsyncRolePermissionApiAdapter(
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
  @Async
  public CompletableFuture<RoleResponse> addPermission(
      @Nonnull Long roleId, @Nonnull Long permissionId) {
    return CompletableFuture.completedFuture(
        roleResponseMapper.toRoleResponse(
            roleAddPermissionUseCase.addPermission(roleId, permissionId)));
  }

  /**
   * Removes a permission from the specified role and returns the updated role as a DTO.
   *
   * @param roleId the ID of the role; must not be null
   * @param permissionId the ID of the permission to remove; must not be null
   * @return a {@link RoleResponse} representing the updated role
   */
  @Override
  @Async
  public CompletableFuture<RoleResponse> removePermission(
      @Nonnull Long roleId, @Nonnull Long permissionId) {
    return CompletableFuture.completedFuture(
        roleResponseMapper.toRoleResponse(
            roleRemovePermissionUseCase.removePermission(roleId, permissionId)));
  }
}
