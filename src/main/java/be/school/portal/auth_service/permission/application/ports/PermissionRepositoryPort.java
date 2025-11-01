package be.school.portal.auth_service.permission.application.ports;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionRepositoryPort {
  Optional<Permission> findById(@Nonnull Long permissionId);

  Permission delete(@Nonnull Permission existingPermission);

  boolean existsByName(String permissionName);

  Permission save(Permission permission);

  Page<Permission> findAllByIdIn(Set<Long> ids, Pageable pageable);
}
