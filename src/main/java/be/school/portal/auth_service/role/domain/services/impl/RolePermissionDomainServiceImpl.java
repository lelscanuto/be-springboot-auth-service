package be.school.portal.auth_service.role.domain.services.impl;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.exceptions.RolePermissionNotExistException;
import be.school.portal.auth_service.role.domain.services.RolePermissionDomainService;
import jakarta.annotation.Nonnull;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link RolePermissionDomainService} that provides domain-level operations
 * for adding and removing permissions from roles.
 *
 * <p>This service enforces business rules related to role-permission management, such as preventing
 * duplicate permissions or removing permissions that do not exist.
 *
 * <p>Transactional behavior is set to {@link Propagation#REQUIRED}, ensuring that permission
 * modifications occur within a transactional context.
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class RolePermissionDomainServiceImpl implements RolePermissionDomainService {

  /**
   * Adds a permission to the specified role.
   *
   * <p>Business rules enforced:
   *
   * <ul>
   *   <li>Throws {@link RolePermissionNotExistException} if the role already has the permission.
   * </ul>
   *
   * @param existingRole the role to which the permission will be added; must not be null
   * @param existingPermission the permission to add; must not be null
   * @throws RolePermissionNotExistException if the role already contains the specified permission
   */
  @Override
  public void addPermission(@Nonnull Role existingRole, @Nonnull Permission existingPermission) {

    // Get the current permissions of the role
    Set<Permission> rolePermissions = existingRole.getPermissions();

    // Check if the role already has the permission
    final var hasPermission =
        rolePermissions.stream().anyMatch(permission -> permission.equals(existingPermission));

    // If the permission already exists, throw an exception
    if (hasPermission) {
      throw RolePermissionNotExistException.ofRoleIdAndPermissionId(
          existingRole.getId(), existingPermission.getId());
    }

    // Add the permission to the role
    existingRole.addPermission(existingPermission);
  }

  /**
   * Removes a permission from the specified role.
   *
   * <p>Business rules enforced:
   *
   * <ul>
   *   <li>Throws {@link RolePermissionNotExistException} if the role does not have the permission.
   * </ul>
   *
   * @param existingRole the role from which the permission will be removed; must not be null
   * @param existingPermission the permission to remove; must not be null
   * @throws RolePermissionNotExistException if the role does not contain the specified permission
   */
  @Override
  public void removePermission(@Nonnull Role existingRole, @Nonnull Permission existingPermission) {

    // Get the current permissions of the role
    Set<Permission> rolePermissions = existingRole.getPermissions();

    // Check if the role has the permission
    final var hasPermission =
        rolePermissions.stream().anyMatch(permission -> permission.equals(existingPermission));

    // If the permission does not exist, throw an exception
    if (!hasPermission) {
      throw RolePermissionNotExistException.ofRoleIdAndPermissionId(
          existingRole.getId(), existingPermission.getId());
    }

    // Remove the permission from the role
    existingRole.removePermission(existingPermission);
  }
}
