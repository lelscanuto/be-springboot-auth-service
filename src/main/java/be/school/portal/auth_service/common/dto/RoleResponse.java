package be.school.portal.auth_service.common.dto;

import java.util.Set;

public record RoleResponse(Long id, String name, Set<String> permissions) {}
