package be.school.portal.auth_service.permission.application.use_cases.impl;

import be.school.portal.auth_service.permission.application.ports.PermissionRepositoryPort;
import be.school.portal.auth_service.permission.application.use_cases.PermissionDeleteUseCase;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.permission.domain.exceptions.PermissionNotFoundException;
import be.school.portal.auth_service.permission.domain.exceptions.PermissionUsedInRoleException;
import be.school.portal.auth_service.role.application.use_cases.RolePermissionLookUpUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PermissionDeleteUseCaseImpl implements PermissionDeleteUseCase {

  private final PermissionRepositoryPort permissionRepositoryPort;
  private final RolePermissionLookUpUseCase rolePermissionLookUpUseCase;

  public PermissionDeleteUseCaseImpl(
      PermissionRepositoryPort permissionRepositoryPort,
      RolePermissionLookUpUseCase rolePermissionLookUpUseCase) {
    this.permissionRepositoryPort = permissionRepositoryPort;
    this.rolePermissionLookUpUseCase = rolePermissionLookUpUseCase;
  }

  @Override
  public Permission delete(Long permissionId) {

    // Look up existing permission
    final var existingPermission =
        permissionRepositoryPort
            .findById(permissionId)
            .orElseThrow(() -> PermissionNotFoundException.ofId(permissionId));

    // Check if permission is assigned to a role
    if (rolePermissionLookUpUseCase.isPermissionUsed(permissionId)) {
      throw PermissionUsedInRoleException.ofName(existingPermission.getName());
    }

    // Delete permission
    return permissionRepositoryPort.delete(existingPermission);
  }
}
