package be.school.portal.auth_service.permission.presentation.api.impl;

import be.school.portal.auth_service.common.dto.CreatePermissionRequest;
import be.school.portal.auth_service.common.dto.PermissionResponse;
import be.school.portal.auth_service.permission.presentation.api.PermissionManagementController;
import be.school.portal.auth_service.permission.presentation.facade.PermissionManagementFacade;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionManagementControllerImpl implements PermissionManagementController {

  private final PermissionManagementFacade permissionManagementFacade;

  public PermissionManagementControllerImpl(PermissionManagementFacade permissionManagementFacade) {
    this.permissionManagementFacade = permissionManagementFacade;
  }

  @Override
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<PermissionResponse> createPermission(
      @RequestBody CreatePermissionRequest createPermissionRequest) {
    return permissionManagementFacade.create(createPermissionRequest);
  }

  @Override
  @DeleteMapping("/{permissionId}")
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<PermissionResponse> deletePermission(@PathVariable Long permissionId) {
    return permissionManagementFacade.delete(permissionId);
  }
}
