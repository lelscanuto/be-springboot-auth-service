package be.school.portal.auth_service.role.presentation.api.impl;

import be.school.portal.auth_service.common.dto.RolePermissionResponse;
import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.role.presentation.api.RoleLookUpController;
import be.school.portal.auth_service.role.presentation.facade.RoleLookUpFacade;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleLookUpControllerImpl implements RoleLookUpController {

  private final RoleLookUpFacade roleLookUpFacade;

  public RoleLookUpControllerImpl(RoleLookUpFacade roleLookUpFacade) {
    this.roleLookUpFacade = roleLookUpFacade;
  }

  @Override
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<Page<RoleResponse>> findAllByFilter(
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "deleted", required = false) Boolean isDeleted,
      @PageableDefault(direction = Sort.Direction.ASC, sort = "id") Pageable pageable) {
    return roleLookUpFacade.findAllByFilter(name, isDeleted, pageable);
  }

  @Override
  @GetMapping("/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<RoleResponse> findById(@PathVariable("roleId") Long roleId) {
    return roleLookUpFacade.findById(roleId);
  }

  @Override
  @GetMapping("/{roleId}/permissions")
  @PreAuthorize("hasRole('ADMIN')")
  public CompletableFuture<RolePermissionResponse> findRolePermissionByRoleId(
      @PathVariable("roleId") Long roleId,
      @PageableDefault(direction = Sort.Direction.ASC, sort = "id") Pageable pageable) {
    return roleLookUpFacade.findRolePermissionByRoleId(roleId, pageable);
  }
}
