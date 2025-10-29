package be.school.portal.auth_service.common.dto;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import java.util.Set;

public record RoleResponse(Long id, String name, Set<Permission> permissions) {}
