package be.school.portal.auth_service.role.presentation.facade;

import be.school.portal.auth_service.common.dto.RoleResponse;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface RolePermissionFacade {
  CompletableFuture<RoleResponse> addPermission(@Nonnull Long roleId, @Nonnull Long permissionId);

  CompletableFuture<RoleResponse> removePermission(@Nonnull Long roleId, @Nonnull Long permissionId);
}
