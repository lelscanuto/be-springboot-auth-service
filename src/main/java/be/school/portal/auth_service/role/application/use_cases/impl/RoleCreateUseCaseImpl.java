package be.school.portal.auth_service.role.application.use_cases.impl;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleCreateUseCase;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.exceptions.RoleAlreadyExistsException;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Implementation of the {@link RoleCreateUseCase} responsible for handling the creation of new
 * roles.
 *
 * <p>This use case ensures that no duplicate role names are created in the system by checking for
 * existing roles before persisting a new one. If a role with the same name already exists, a {@link
 * be.school.portal.auth_service.role.domain.exceptions.RoleAlreadyExistsException} is thrown.
 *
 * <p>The class is marked as a Spring {@link org.springframework.stereotype.Service} and is {@link
 * org.springframework.transaction.annotation.Transactional transactional} to ensure data
 * consistency within a new transaction boundary.
 *
 * <p>Validation is applied using Spring's {@link
 * org.springframework.validation.annotation.Validated} annotation to enforce constraints defined on
 * method parameters.
 */
@Service
@Validated
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RoleCreateUseCaseImpl implements RoleCreateUseCase {

  private final RoleRepositoryPort roleRepositoryPort;

  /**
   * Constructs a new {@code RoleCreateUseCaseImpl} with the required repository port.
   *
   * @param roleRepositoryPort the repository port used to access role persistence operations
   */
  public RoleCreateUseCaseImpl(RoleRepositoryPort roleRepositoryPort) {
    this.roleRepositoryPort = roleRepositoryPort;
  }

  /**
   * Creates a new role in the system.
   *
   * <p>This method performs the following steps:
   *
   * <ol>
   *   <li>Validates the provided {@link CreateRoleRequest}.
   *   <li>Checks if a role with the same name already exists.
   *   <li>If no existing role is found, a new {@link Role} is created and persisted.
   * </ol>
   *
   * @param createRoleRequest the request containing role creation details; must not be {@code null}
   * @return the newly created {@link Role}
   * @throws be.school.portal.auth_service.role.domain.exceptions.RoleAlreadyExistsException if a
   *     role with the same name already exists
   */
  @Override
  public Role create(@Valid @Nonnull CreateRoleRequest createRoleRequest) {

    // Check if a role with the same name already exists
    if (roleRepositoryPort.existsByName(createRoleRequest.name())) {
      throw RoleAlreadyExistsException.ofName(createRoleRequest.name());
    }

    // Create and save the new role
    return roleRepositoryPort.save(Role.withName(createRoleRequest.name()));
  }
}
