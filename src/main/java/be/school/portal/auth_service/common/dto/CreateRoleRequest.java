package be.school.portal.auth_service.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(@NotBlank @Size(min = 3, max = 100) String name) {}
