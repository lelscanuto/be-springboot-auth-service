package be.school.portal.auth_service.role.application.use_cases.impl;

import be.school.portal.auth_service.account.application.use_cases.UserLookUpUseCase;
import be.school.portal.auth_service.role.application.port.RoleRepositoryPort;
import be.school.portal.auth_service.role.application.use_cases.RoleDeleteUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleLookUpUseCase;
import be.school.portal.auth_service.role.domain.entities.Role;
import be.school.portal.auth_service.role.domain.exceptions.RoleAssignedToActiveUserException;
import be.school.portal.auth_service.role.domain.exceptions.RoleNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Use case implementation responsible for handling the deletion of {@link Role} entities.
 *
 * <p>This service ensures that a role cannot be deleted if it is still assigned to any active
 * users. It uses {@link RoleRepositoryPort} to interact with role persistence and {@link
 * UserLookUpUseCase} to verify user-role relationships.
 *
 * <p>The method runs in a new transactional context ({@link
 * org.springframework.transaction.annotation.Propagation#REQUIRES_NEW}) to ensure that the delete
 * operation is isolated and committed independently from any parent transactions.
 *
 * <p>Validation annotations such as {@link jakarta.validation.constraints.NotNull} and {@link
 * javax.annotation.Nonnull} are used to enforce input constraints.
 *
 * <p>Throws a {@link RoleNotFoundException} if the role does not exist, or a {@link
 * RoleAssignedToActiveUserException} if the role is still in use by any active users.
 *
 * @see RoleRepositoryPort
 * @see UserLookUpUseCase
 * @see RoleAssignedToActiveUserException
 * @see RoleNotFoundException
 */
@Service
@Validated
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RoleDeleteUseCaseImpl implements RoleDeleteUseCase {

  private final RoleLookUpUseCase roleLookUpUseCase;
  private final RoleRepositoryPort roleRepositoryPort;
  private final UserLookUpUseCase userLookUpUseCase;

  /**
   * Constructs a {@code RoleDeleteUseCaseImpl} with required dependencies.
   *
   * @param roleRepositoryPort the repository port for role persistence operations
   * @param userLookUpUseCase the use case for checking user-role relationships
   */
  public RoleDeleteUseCaseImpl(
      RoleLookUpUseCase roleLookUpUseCase,
      RoleRepositoryPort roleRepositoryPort,
      UserLookUpUseCase userLookUpUseCase) {
    this.roleLookUpUseCase = roleLookUpUseCase;
    this.roleRepositoryPort = roleRepositoryPort;
    this.userLookUpUseCase = userLookUpUseCase;
  }

  /**
   * Deletes the specified role if it is not assigned to any active users.
   *
   * <p>The method performs the following steps:
   *
   * <ol>
   *   <li>Retrieves the existing role by its ID.
   *   <li>Validates that the role is not assigned to any active users.
   *   <li>Deletes the role using the {@link RoleRepositoryPort}.
   * </ol>
   *
   * @param roleId the ID of the role to delete (must not be null)
   * @return the deleted {@link Role} instance
   * @throws RoleNotFoundException if no role exists with the given ID
   * @throws RoleAssignedToActiveUserException if the role is assigned to any active users
   */
  @Override
  public Role delete(@NotNull @Nonnull Long roleId) {

    // Verify role exists
    final var existingRole = roleLookUpUseCase.findById(roleId);

    // Check if role is assigned to any user
    if (userLookUpUseCase.existsByRole(existingRole.getName())) {
      throw RoleAssignedToActiveUserException.ofName(existingRole.getName());
    }

    // Proceed to delete the role
    return roleRepositoryPort.delete(existingRole);
  }
}
