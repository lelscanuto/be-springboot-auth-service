package be.school.portal.auth_service.common.dto;

import be.school.portal.auth_service.common.annotations.Hide;
import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank @Hide(Hide.MaskStrategy.OMIT) String token) {}
