package be.school.portal.auth_service.role.application.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.dto.RolePermissionResponse;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.logging.LoggerName;
import be.school.portal.auth_service.role.application.mappers.RolePermissionMapper;
import be.school.portal.auth_service.role.application.mappers.RoleResponseMapper;
import be.school.portal.auth_service.role.application.use_cases.RoleLookUpUseCase;
import be.school.portal.auth_service.role.application.use_cases.RolePermissionLookUpUseCase;
import be.school.portal.auth_service.role.presentation.facade.RoleLookUpFacade;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Trace(logger = LoggerName.API_LOGGER)
public class AsyncRoleLookUpApiAdapter implements RoleLookUpFacade {

  private final RoleLookUpUseCase roleLookUpUseCase;
  private final RolePermissionLookUpUseCase rolePermissionLookUpUseCase;
  private final RoleResponseMapper roleResponseMapper;
  private final RolePermissionMapper rolePermissionMapper;

  public AsyncRoleLookUpApiAdapter(
      RoleLookUpUseCase roleLookUpUseCase,
      RolePermissionLookUpUseCase rolePermissionLookUpUseCase,
      RoleResponseMapper roleResponseMapper,
      RolePermissionMapper rolePermissionMapper) {
    this.roleLookUpUseCase = roleLookUpUseCase;
    this.rolePermissionLookUpUseCase = rolePermissionLookUpUseCase;
    this.roleResponseMapper = roleResponseMapper;
    this.rolePermissionMapper = rolePermissionMapper;
  }

  @Override
  @Async
  public CompletableFuture<Page<RoleResponse>> findAllByFilter(
      @Nullable String name, @Nonnull Boolean isDeleted, @Nonnull Pageable pageable) {
    return CompletableFuture.completedFuture(
        roleLookUpUseCase
            .findAllByFilter(name, isDeleted, pageable)
            .map(roleResponseMapper::toRoleResponse));
  }

  @Override
  @Async
  public CompletableFuture<RoleResponse> findById(@Nonnull Long roleId) {
    return CompletableFuture.completedFuture(
        roleResponseMapper.toRoleResponse(roleLookUpUseCase.findById(roleId)));
  }

  @Override
  @Async
  public CompletableFuture<RolePermissionResponse> findRolePermissionByRoleId(
      @Nonnull Long roleId, @Nonnull Pageable pageable) {
    return CompletableFuture.completedFuture(
        rolePermissionMapper.toRolePermissionResponse(
            rolePermissionLookUpUseCase.findAllByRoleId(roleId, pageable)));
  }
}
