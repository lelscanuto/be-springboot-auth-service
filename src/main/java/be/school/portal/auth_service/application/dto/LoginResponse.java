package be.school.portal.auth_service.application.dto;

import java.util.Set;

public record LoginResponse(String token, String username, Set<String> roles) {}
