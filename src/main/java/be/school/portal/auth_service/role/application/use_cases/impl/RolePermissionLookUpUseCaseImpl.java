package be.school.portal.auth_service.role.application.use_cases.impl;

import be.school.portal.auth_service.permission.application.ports.PermissionRepositoryPort;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.application.dto.RolePageablePermissionDTO;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleLookUpUseCase;
import be.school.portal.auth_service.role.application.use_cases.RolePermissionLookUpUseCase;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class RolePermissionLookUpUseCaseImpl implements RolePermissionLookUpUseCase {

  private final RoleLookUpUseCase roleLookUpUseCase;
  private final PermissionRepositoryPort permissionRepositoryPort;
  private final RoleRepositoryPort roleRepositoryPort;

  public RolePermissionLookUpUseCaseImpl(
      RoleLookUpUseCase roleLookUpUseCase,
      PermissionRepositoryPort permissionRepositoryPort,
      RoleRepositoryPort roleRepositoryPort) {
    this.roleLookUpUseCase = roleLookUpUseCase;
    this.permissionRepositoryPort = permissionRepositoryPort;
    this.roleRepositoryPort = roleRepositoryPort;
  }

  @Override
  public boolean isPermissionUsed(Long permissionId) {
    return !roleRepositoryPort.findAllByIsDeletedFalseAndPermissions_Id(permissionId).isEmpty();
  }

  @Override
  public RolePageablePermissionDTO findAllByRoleId(Long roleId, Pageable pageable) {

    final var existingRole = roleLookUpUseCase.findById(roleId);

    final var pageablePermissions =
        permissionRepositoryPort.findAllByIdIn(
            existingRole.getPermissions().stream()
                .map(Permission::getId)
                .collect(Collectors.toSet()),
            pageable);

    return new RolePageablePermissionDTO(existingRole.getId(), pageablePermissions);
  }
}
