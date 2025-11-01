package be.school.portal.auth_service.common.dto;

import org.springframework.data.domain.Page;

public record RolePermissionResponse(Long roleId, Page<PermissionResponse> permissions) {}
