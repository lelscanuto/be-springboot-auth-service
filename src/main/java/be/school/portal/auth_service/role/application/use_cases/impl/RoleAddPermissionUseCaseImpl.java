package be.school.portal.auth_service.role.application.use_cases.impl;

import be.school.portal.auth_service.permission.application.use_cases.PermissionLookUpUseCase;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleAddPermissionUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleLookUpUseCase;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.services.RolePermissionDomainService;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RoleAddPermissionUseCaseImpl implements RoleAddPermissionUseCase {

  private final RoleLookUpUseCase roleLookUpUseCase;
  private final RoleRepositoryPort roleRepositoryPort;
  private final RolePermissionDomainService rolePermissionDomainService;
  private final PermissionLookUpUseCase permissionLookUpUseCase;

  public RoleAddPermissionUseCaseImpl(
      RoleLookUpUseCase roleLookUpUseCase,
      RoleRepositoryPort roleRepositoryPort,
      RolePermissionDomainService rolePermissionDomainService,
      PermissionLookUpUseCase permissionLookUpUseCase) {
    this.roleLookUpUseCase = roleLookUpUseCase;
    this.roleRepositoryPort = roleRepositoryPort;
    this.rolePermissionDomainService = rolePermissionDomainService;
    this.permissionLookUpUseCase = permissionLookUpUseCase;
  }

  @Override
  public Role addPermission(@Nonnull Long roleId, @Nonnull Long permissionId) {

    // Lookup existing role
    final var existingRole =
        roleLookUpUseCase.findById(roleId);

    // Lookup existing permission
    final var existingPermission = permissionLookUpUseCase.lookupById(permissionId);

    // Add permission to role
    rolePermissionDomainService.addPermission(existingRole, existingPermission);

    // Save updated role
    return roleRepositoryPort.save(existingRole);
  }
}
