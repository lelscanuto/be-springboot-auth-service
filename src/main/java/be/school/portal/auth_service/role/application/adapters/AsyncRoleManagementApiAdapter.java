package be.school.portal.auth_service.role.application.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import be.school.portal.auth_service.role.application.mappers.RoleResponseMapper;
import be.school.portal.auth_service.role.application.use_cases.RoleCreateUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleDeleteUseCase;
import be.school.portal.auth_service.role.application.use_cases.RoleUpdateUseCase;
import be.school.portal.auth_service.role.presentation.facade.RoleManagementFacade;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Trace(logArgsAndResult = true)
public class AsyncRoleManagementApiAdapter implements RoleManagementFacade {

  private final RoleCreateUseCase roleCreateUseCase;
  private final RoleUpdateUseCase roleUpdateUseCase;
  private final RoleDeleteUseCase roleDeleteUseCase;
  private final RoleResponseMapper roleResponseMapper;

  public AsyncRoleManagementApiAdapter(
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
  @Async
  public CompletableFuture<RoleResponse> createRole(@Nonnull CreateRoleRequest createRoleRequest) {
    return CompletableFuture.completedFuture(
        roleResponseMapper.toRoleResponse(roleCreateUseCase.create(createRoleRequest)));
  }

  @Override
  @Async
  public CompletableFuture<RoleResponse> updateRole(
      @Nonnull Long id, UpdateRoleRequest updateRoleRequest) {
    return CompletableFuture.completedFuture(
        roleResponseMapper.toRoleResponse(roleUpdateUseCase.update(id, updateRoleRequest)));
  }

  @Override
  @Async
  public CompletableFuture<RoleResponse> deleteRole(@Nonnull Long roleId) {
    return CompletableFuture.completedFuture(
        roleResponseMapper.toRoleResponse(roleDeleteUseCase.delete(roleId)));
  }
}
