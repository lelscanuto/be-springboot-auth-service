package be.school.portal.auth_service.role.presentation.facade;

import be.school.portal.auth_service.common.dto.RolePermissionResponse;
import be.school.portal.auth_service.common.dto.RoleResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleLookUpFacade {

  CompletableFuture<Page<RoleResponse>> findAllByFilter(
      @Nullable String name, @Nonnull Boolean isDeleted, @Nonnull Pageable pageable);

  CompletableFuture<RoleResponse> findById(@Nonnull Long roleId);

  CompletableFuture<RolePermissionResponse> findRolePermissionByRoleId(
      @Nonnull Long roleId, @Nonnull Pageable pageable);
}
