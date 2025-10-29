package be.school.portal.auth_service.role.infrastructure.repositories.adapters;

import be.school.portal.auth_service.common.aspect.Trace;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.infrastructure.repositories.RoleRepository;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleRepositoryJpaAdapter implements RoleRepositoryPort {

  private final RoleRepository roleRepository;

  public RoleRepositoryJpaAdapter(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  @Trace
  public Optional<Role> findById(@Nonnull Long roleId) {
    return roleRepository.findById(roleId);
  }

  @Override
  @Trace
  public Role save(@Nonnull Role existingRole) {
    return roleRepository.save(existingRole);
  }

  @Override
  public Boolean existsByName(@Nonnull String name) {
    return roleRepository.existsByName(name);
  }

  @Override
  public Role delete(@Nonnull Role existingRole) {
    existingRole.delete();
    return roleRepository.save(existingRole);
  }
}
