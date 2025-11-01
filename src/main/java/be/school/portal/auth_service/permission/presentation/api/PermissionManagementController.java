package be.school.portal.auth_service.permission.presentation.api;

import be.school.portal.auth_service.common.dto.CreatePermissionRequest;
import be.school.portal.auth_service.common.dto.PermissionResponse;
import java.util.concurrent.CompletableFuture;

public interface PermissionManagementController {

  CompletableFuture<PermissionResponse> createPermission(
      CreatePermissionRequest createPermissionRequest);

  CompletableFuture<PermissionResponse> deletePermission(Long id);
}
