package be.school.portal.auth_service.role.application.use_cases;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;

public interface RoleCreateUseCase {
  Role create(@Valid @Nonnull CreateRoleRequest createRoleRequest);
}
