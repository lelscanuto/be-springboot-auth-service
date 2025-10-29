package be.school.portal.auth_service.role.application.use_cases.impl;

import be.school.portal.auth_service.permission.application.use_cases.PermissionLookUpUseCase;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleRemovePermissionUseCase;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.exceptions.RoleNotFoundException;
import be.school.portal.auth_service.role.domain.exceptions.RolePermissionNotExistException;
import be.school.portal.auth_service.role.domain.services.RolePermissionDomainService;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link RoleRemovePermissionUseCase} that handles the use case of removing a
 * permission from a role.
 *
 * <p>This use case orchestrates the retrieval of the role and permission, delegates the removal
 * logic to the {@link RolePermissionDomainService}, and persists the updated role.
 *
 * <p>Transactional behavior is configured with {@link Propagation#REQUIRES_NEW} to ensure that the
 * operation runs in a separate transaction context.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RoleRemovePermissionUseCaseImpl implements RoleRemovePermissionUseCase {

  private final RoleRepositoryPort roleRepositoryPort;
  private final RolePermissionDomainService rolePermissionDomainService;
  private final PermissionLookUpUseCase permissionLookUpUseCase;

  /**
   * Constructs a new {@link RoleRemovePermissionUseCaseImpl}.
   *
   * @param roleRepositoryPort the port to access role persistence
   * @param rolePermissionDomainService the domain service for role-permission business rules
   * @param permissionLookUpUseCase the use case to retrieve permissions
   */
  public RoleRemovePermissionUseCaseImpl(
      RoleRepositoryPort roleRepositoryPort,
      RolePermissionDomainService rolePermissionDomainService,
      PermissionLookUpUseCase permissionLookUpUseCase) {
    this.roleRepositoryPort = roleRepositoryPort;
    this.rolePermissionDomainService = rolePermissionDomainService;
    this.permissionLookUpUseCase = permissionLookUpUseCase;
  }

  /**
   * Removes a permission from a role.
   *
   * <p>Steps performed:
   *
   * <ol>
   *   <li>Retrieve the existing role by {@code roleId}. Throws {@link RoleNotFoundException} if not
   *       found.
   *   <li>Retrieve the existing permission by {@code permissionId} using {@link
   *       PermissionLookUpUseCase}.
   *   <li>Delegates the removal logic to {@link RolePermissionDomainService#removePermission(Role,
   *       Permission)}.
   *   <li>Persists and returns the updated role.
   * </ol>
   *
   * @param roleId the ID of the role from which the permission will be removed; must not be null
   * @param permissionId the ID of the permission to remove; must not be null
   * @return the updated {@link Role} after the permission has been removed
   * @throws RoleNotFoundException if the role with {@code roleId} does not exist
   * @throws RolePermissionNotExistException if the role does not have the specified permission
   */
  @Override
  public Role removePermission(@Nonnull Long roleId, @Nonnull Long permissionId) {

    // Lookup existing role
    final var existingRole =
        roleRepositoryPort.findById(roleId).orElseThrow(() -> RoleNotFoundException.ofId(roleId));

    // Lookup existing permission
    final var existingPermission = permissionLookUpUseCase.lookupById(permissionId);

    // Remove permission from role
    rolePermissionDomainService.removePermission(existingRole, existingPermission);

    // Save updated role
    return roleRepositoryPort.save(existingRole);
  }
}
