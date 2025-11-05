package be.school.portal.auth_service.role.application.use_cases.impl;

import static be.school.portal.auth_service.role.infrastructure.persistence.specifications.RoleSpecificationFactory.byStatus;

import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleLookUpUseCase;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.exceptions.RoleNotFoundException;
import be.school.portal.auth_service.role.infrastructure.persistence.specifications.RoleSpecificationFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class RoleLookUpUseCaseImpl implements RoleLookUpUseCase {

  private final RoleRepositoryPort roleRepositoryPort;

  public RoleLookUpUseCaseImpl(RoleRepositoryPort roleRepositoryPort) {
    this.roleRepositoryPort = roleRepositoryPort;
  }

  @Override
  public Page<Role> findAllByFilter(
      @Nullable String name, @Nullable Boolean isDeleted, @Nonnull Pageable pageable) {
    return roleRepositoryPort.findAll(
        RoleSpecificationFactory.byName(name).and(byStatus(isDeleted)), pageable);
  }

  @Override
  public Role findById(@Nonnull Long roleId) {
    return roleRepositoryPort
        .findById(roleId)
        .orElseThrow(() -> RoleNotFoundException.ofId(roleId));
  }
}
