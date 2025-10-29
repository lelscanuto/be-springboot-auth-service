package be.school.portal.auth_service.permission.application.ports;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import jakarta.annotation.Nonnull;
import java.util.Optional;

public interface PermissionRepositoryPort {
  Optional<Permission> findById(@Nonnull Long permissionId);
}
