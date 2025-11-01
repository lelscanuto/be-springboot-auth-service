package be.school.portal.auth_service.role.application.dto;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import org.springframework.data.domain.Page;

public record RolePageablePermissionDTO(Long roleId, Page<Permission> permissionPageable) {}
