package be.school.portal.auth_service.account.domain.projections;

import be.school.portal.auth_service.account.domain.enums.UserStatus;
import java.util.Set;

public record UserProjection(
    String username,
    String password,
    UserStatus status,
    Set<String> roles,
    Set<String> permissions) {}
