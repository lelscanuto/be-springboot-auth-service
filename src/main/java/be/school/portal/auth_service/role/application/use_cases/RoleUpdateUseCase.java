package be.school.portal.auth_service.role.application.use_cases;

import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface RoleUpdateUseCase {
  Role update(@Nonnull Long id, @Nonnull @Valid UpdateRoleRequest updateRoleRequest);
}
