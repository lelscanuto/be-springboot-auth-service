package be.school.portal.auth_service.permission.presentation.facade;

import be.school.portal.auth_service.common.dto.CreatePermissionRequest;
import be.school.portal.auth_service.common.dto.PermissionResponse;
import java.util.concurrent.CompletableFuture;

public interface PermissionManagementFacade {
  CompletableFuture<PermissionResponse> create(CreatePermissionRequest createPermissionRequest);

  CompletableFuture<PermissionResponse> delete(Long permissionId);
}
