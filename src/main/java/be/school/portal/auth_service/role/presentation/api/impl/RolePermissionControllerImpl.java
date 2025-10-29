package be.school.portal.auth_service.role.presentation.api.impl;

import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.role.presentation.api.RolePermissionController;
import be.school.portal.auth_service.role.presentation.facade.RolePermissionFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles/{roleId}/permissions/{permissionId}")
public class RolePermissionControllerImpl implements RolePermissionController {

  private final RolePermissionFacade rolePermissionFacade;

  public RolePermissionControllerImpl(RolePermissionFacade rolePermissionFacade) {
    this.rolePermissionFacade = rolePermissionFacade;
  }

  @Override
  @PutMapping
  @PreAuthorize("hasRole('ADMIN')")
  public RoleResponse addPermission(
      @PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
    return rolePermissionFacade.addPermission(roleId, permissionId);
  }

  @Override
  @DeleteMapping
  @PreAuthorize("hasRole('ADMIN')")
  public RoleResponse removePermission(
      @PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
    return rolePermissionFacade.removePermission(roleId, permissionId);
  }
}
