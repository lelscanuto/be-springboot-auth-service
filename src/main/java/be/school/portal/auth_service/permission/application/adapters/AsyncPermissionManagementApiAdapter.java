package be.school.portal.auth_service.permission.application.adapters;

import be.school.portal.auth_service.common.annotations.Trace;
import be.school.portal.auth_service.common.dto.CreatePermissionRequest;
import be.school.portal.auth_service.common.dto.PermissionResponse;
import be.school.portal.auth_service.common.logging.LoggerName;
import be.school.portal.auth_service.permission.application.mappers.PermissionResponseMapper;
import be.school.portal.auth_service.permission.application.use_cases.PermissionCreateUseCase;
import be.school.portal.auth_service.permission.application.use_cases.PermissionDeleteUseCase;
import be.school.portal.auth_service.permission.presentation.facade.PermissionManagementFacade;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Trace(logger = LoggerName.ADAPTER_LOGGER)
public class AsyncPermissionManagementApiAdapter implements PermissionManagementFacade {

  private final PermissionCreateUseCase permissionCreateUseCase;
  private final PermissionDeleteUseCase permissionDeleteUseCase;
  private final PermissionResponseMapper permissionResponseMapper;

  public AsyncPermissionManagementApiAdapter(
      PermissionCreateUseCase permissionCreateUseCase,
      PermissionDeleteUseCase permissionDeleteUseCase,
      PermissionResponseMapper permissionResponseMapper) {
    this.permissionCreateUseCase = permissionCreateUseCase;
    this.permissionDeleteUseCase = permissionDeleteUseCase;
    this.permissionResponseMapper = permissionResponseMapper;
  }

  @Override
  @Async
  public CompletableFuture<PermissionResponse> create(
      CreatePermissionRequest createPermissionRequest) {
    return CompletableFuture.completedFuture(
        permissionResponseMapper.toPermissionResponse(
            permissionCreateUseCase.create(createPermissionRequest)));
  }

  @Override
  @Async
  public CompletableFuture<PermissionResponse> delete(Long permissionId) {
    return CompletableFuture.completedFuture(
        permissionResponseMapper.toPermissionResponse(
            permissionDeleteUseCase.delete(permissionId)));
  }
}
