package be.school.portal.auth_service.permission.infrastructure.repositories.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.logging.LoggerName;
import be.school.portal.auth_service.permission.application.ports.PermissionRepositoryPort;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.permission.infrastructure.repositories.PermissionRepository;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Trace(logger = LoggerName.REPOSITORY_LOGGER)
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

  @Override
  public Permission delete(@Nonnull Permission existingPermission) {
    permissionRepository.delete(existingPermission);

    return existingPermission;
  }

  @Override
  public boolean existsByName(String permissionName) {
    return permissionRepository.existsByName(permissionName);
  }

  @Override
  public Permission save(Permission permission) {
    return permissionRepository.save(permission);
  }

  @Override
  public Page<Permission> findAllByIdIn(Set<Long> ids, Pageable pageable) {
    return permissionRepository.findAllByIdIn(ids, pageable);
  }
}
