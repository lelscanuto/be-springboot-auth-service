package be.school.portal.auth_service.permission.application.use_cases.impl;

import be.school.portal.auth_service.common.dto.CreatePermissionRequest;
import be.school.portal.auth_service.permission.application.ports.PermissionRepositoryPort;
import be.school.portal.auth_service.permission.application.use_cases.PermissionCreateUseCase;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.permission.domain.exceptions.PermissionAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PermissionCreateUseCaseImpl implements PermissionCreateUseCase {

  private final PermissionRepositoryPort permissionRepositoryPort;

  public PermissionCreateUseCaseImpl(PermissionRepositoryPort permissionRepositoryPort) {
    this.permissionRepositoryPort = permissionRepositoryPort;
  }

  @Override
  public Permission create(CreatePermissionRequest createPermissionRequest) {

    // Extract permission name from request
    final var permissionName = createPermissionRequest.permissionName();

    // Check if permission with the same name already exists
    if (permissionRepositoryPort.existsByName(permissionName)) {
      throw PermissionAlreadyExistsException.ofName(permissionName);
    }

    // Create and save new permission
    return permissionRepositoryPort.save(Permission.withName(permissionName));
  }
}
