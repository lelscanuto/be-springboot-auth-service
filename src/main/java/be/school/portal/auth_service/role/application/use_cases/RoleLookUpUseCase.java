package be.school.portal.auth_service.role.application.use_cases;

import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleLookUpUseCase {
  Page<Role> findAllByFilter(
      @Nullable String name, @Nullable Boolean isDeleted, @Nonnull Pageable pageable);

  Role findById(@Nonnull Long roleId);
}
