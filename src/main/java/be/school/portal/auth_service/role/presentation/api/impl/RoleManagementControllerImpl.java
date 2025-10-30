package be.school.portal.auth_service.role.presentation.api.impl;

import be.school.portal.auth_service.common.dto.CreateRoleRequest;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.common.dto.UpdateRoleRequest;
import be.school.portal.auth_service.role.presentation.api.RoleManagementController;
import be.school.portal.auth_service.role.presentation.facade.RoleManagementFacade;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleManagementControllerImpl implements RoleManagementController {

  private final RoleManagementFacade roleManagementFacade;

  public RoleManagementControllerImpl(RoleManagementFacade roleManagementFacade) {
    this.roleManagementFacade = roleManagementFacade;
  }

  @Override
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<RoleResponse> createRole(@RequestBody CreateRoleRequest createRoleRequest) {
    return roleManagementFacade.createRole(createRoleRequest);
  }

  @Override
  @PatchMapping("/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<RoleResponse> updateRole(
      @PathVariable("roleId") Long id, @RequestBody UpdateRoleRequest updateRoleRequest) {
    return roleManagementFacade.updateRole(id, updateRoleRequest);
  }

  @Override
  @DeleteMapping("/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<RoleResponse> deleteRole(@PathVariable("roleId") Long roleId) {
    return roleManagementFacade.deleteRole(roleId);
  }
}
