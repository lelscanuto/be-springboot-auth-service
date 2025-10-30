package be.school.portal.auth_service.permission.infrastructure.repositories.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.permission.application.ports.PermissionRepositoryPort;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.permission.infrastructure.repositories.PermissionRepository;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Trace
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class PermissionRepositoryJpaAdapter implements PermissionRepositoryPort {

  private final PermissionRepository permissionRepository;

  public PermissionRepositoryJpaAdapter(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  @Override
  public Optional<Permission> findById(@Nonnull Long permissionId) {
    return permissionRepository.findById(permissionId);
  }
}
