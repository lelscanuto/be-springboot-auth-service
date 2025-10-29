package be.school.portal.auth_service.role.application.use_cases.impl;

import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleUpdateUseCase;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.exceptions.RoleNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Implementation of the {@link RoleUpdateUseCase} that handles updating existing role details.
 *
 * <p>This use case retrieves the role by its identifier, updates the provided fields, and persists
 * the changes using the {@link RoleRepositoryPort}. The operation runs in a new transactional
 * context to ensure isolation from other use case executions.
 *
 * <p>Validation is performed on the input parameters to ensure data integrity.
 *
 * <p><strong>Transactional behavior:</strong> {@code REQUIRES_NEW} — starts a new transaction for
 * this update operation regardless of any existing one.
 *
 * @see RoleRepositoryPort
 * @see RoleNotFoundException
 */
@Service
@Validated
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RoleUpdateUseCaseImpl implements RoleUpdateUseCase {

  private final RoleRepositoryPort roleRepositoryPort;

  public RoleUpdateUseCaseImpl(RoleRepositoryPort roleRepositoryPort) {
    this.roleRepositoryPort = roleRepositoryPort;
  }

  /**
   * Updates an existing role with the given details.
   *
   * <p>This method first checks if the role exists in the repository. If found, it updates the
   * role’s name and persists the changes. If no role is found with the specified ID, a {@link
   * RoleNotFoundException} is thrown.
   *
   * @param roleId the unique identifier of the role to update (must not be {@code null})
   * @param updateRoleRequest the validated request object containing new role details
   * @return the updated {@link Role} entity after persistence
   * @throws RoleNotFoundException if no role exists with the given {@code roleId}
   */
  @Override
  public Role update(
      @NotNull @Nonnull Long roleId, @Valid @Nonnull UpdateRoleRequest updateRoleRequest) {

    // Retrieve existing role
    final var existingRole =
        roleRepositoryPort.findById(roleId).orElseThrow(() -> RoleNotFoundException.ofId(roleId));

    // Update role fields
    existingRole.setName(updateRoleRequest.name());

    // Save updated role
    return roleRepositoryPort.save(existingRole);
  }
}
