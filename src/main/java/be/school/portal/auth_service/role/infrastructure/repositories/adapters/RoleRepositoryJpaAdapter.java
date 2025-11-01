package be.school.portal.auth_service.role.infrastructure.repositories.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.infrastructure.repositories.RoleRepository;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Trace
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class RoleRepositoryJpaAdapter implements RoleRepositoryPort {

  private final RoleRepository roleRepository;

  public RoleRepositoryJpaAdapter(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public Optional<Role> findById(@Nonnull Long roleId) {
    return roleRepository.findById(roleId);
  }

  @Override
  public Role save(@Nonnull Role existingRole) {
    return roleRepository.save(existingRole);
  }

  @Override
  public boolean existsByName(@Nonnull String name) {
    return roleRepository.existsByName(name);
  }

  @Override
  public Role delete(@Nonnull Role existingRole) {
    existingRole.delete();
    return roleRepository.save(existingRole);
  }

  @Override
  public List<Role> findAllByIsDeletedFalseAndPermissions_Id(@Nonnull Long permissionId) {
    return roleRepository.findAllByIsDeletedFalseAndPermissions_Id(permissionId);
  }

  @Override
  public Page<Role> findAll(Specification<Role> specification, Pageable pageable) {
    return roleRepository.findAll(specification, pageable);
  }
}
