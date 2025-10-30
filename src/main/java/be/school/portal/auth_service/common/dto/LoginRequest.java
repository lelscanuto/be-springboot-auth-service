package be.school.portal.auth_service.common.dto;

import be.school.portal.auth_service.common.annotations.Hide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Request payload for user login")
public record LoginRequest(
    @NotBlank @Schema(description = "Username of the user", example = "john.doe") String username,
    @NotBlank
        @Schema(description = "Password of the user", example = "P@ssw0rd")
        @Hide(Hide.MaskStrategy.MASK)
        String password) {}
