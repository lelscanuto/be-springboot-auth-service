package be.school.portal.auth_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(name = "LoginResponse", description = "Response payload after successful login")
public record LoginResponse(
    @Schema(
            description = "JWT access token for authentication",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
    @Schema(
            description = "JWT refresh token used to obtain new access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken,
    @Schema(description = "Username of the authenticated user", example = "john.doe")
        String username,
    @Schema(description = "Roles assigned to the user", example = "[\"ADMIN\"]")
        Set<String> roles) {}
