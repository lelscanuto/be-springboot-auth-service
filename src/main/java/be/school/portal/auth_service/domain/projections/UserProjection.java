package be.school.portal.auth_service.domain.projections;

import java.util.Set;

public record UserProjection(String username, String status, Set<RoleProjection> roles) {

  public record RoleProjection(String name, Set<String> permissions) {}
}
