package be.school.portal.auth_service.permission.application.use_cases;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import jakarta.annotation.Nonnull;

public interface PermissionLookUpUseCase {
  Permission lookupById(@Nonnull Long permissionId);
}
