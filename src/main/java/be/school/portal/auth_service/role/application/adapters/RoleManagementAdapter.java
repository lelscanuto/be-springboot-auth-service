package be.school.portal.auth_service.role.application.adapters;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import be.school.portal.auth_service.role.application.mappers.RoleResponseMapper;
import be.school.portal.auth_service.role.application.use_cases.RoleCreateUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleDeleteUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleUpdateUseCase;
import be.school.portal.auth_service.role.presentation.facade.RoleManagementFacade;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class RoleManagementAdapter implements RoleManagementFacade {

  private final RoleCreateUseCase roleCreateUseCase;
  private final RoleUpdateUseCase roleUpdateUseCase;
  private final RoleDeleteUseCase roleDeleteUseCase;
  private final RoleResponseMapper roleResponseMapper;

  public RoleManagementAdapter(
      RoleCreateUseCase roleCreateUseCase,
      RoleUpdateUseCase roleUpdateUseCase,
      RoleDeleteUseCase roleDeleteUseCase,
      RoleResponseMapper roleResponseMapper) {
    this.roleCreateUseCase = roleCreateUseCase;
    this.roleUpdateUseCase = roleUpdateUseCase;
    this.roleDeleteUseCase = roleDeleteUseCase;
    this.roleResponseMapper = roleResponseMapper;
  }

  @Override
  public RoleResponse createRole(@Nonnull CreateRoleRequest createRoleRequest) {
    return roleResponseMapper.toRoleResponse(roleCreateUseCase.create(createRoleRequest));
  }

  @Override
  public RoleResponse updateRole(@Nonnull Long id, UpdateRoleRequest updateRoleRequest) {
    return roleResponseMapper.toRoleResponse(roleUpdateUseCase.update(id, updateRoleRequest));
  }

  @Override
  public RoleResponse deleteRole(@Nonnull Long roleId) {
    return roleResponseMapper.toRoleResponse(roleDeleteUseCase.delete(roleId));
  }
}
