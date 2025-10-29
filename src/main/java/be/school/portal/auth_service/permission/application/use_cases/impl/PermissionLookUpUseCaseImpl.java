package be.school.portal.auth_service.permission.application.use_cases.impl;

import be.school.portal.auth_service.permission.application.ports.PermissionRepositoryPort;
import be.school.portal.auth_service.permission.application.use_cases.PermissionLookUpUseCase;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.permission.domain.exceptions.PermissionNotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionLookUpUseCaseImpl implements PermissionLookUpUseCase {

  private final PermissionRepositoryPort permissionRepositoryPort;

  public PermissionLookUpUseCaseImpl(PermissionRepositoryPort permissionRepositoryPort) {
    this.permissionRepositoryPort = permissionRepositoryPort;
  }

  @Override
  public Permission lookupById(@Nonnull Long permissionId) {
    return permissionRepositoryPort
        .findById(permissionId)
        .orElseThrow(() -> PermissionNotFoundException.ofId(permissionId));
  }
}
