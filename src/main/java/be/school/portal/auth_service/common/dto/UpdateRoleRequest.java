package be.school.portal.auth_service.common.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(@NotBlank String name) {}
