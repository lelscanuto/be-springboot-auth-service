package be.school.portal.auth_service.application.mappers;

import be.school.portal.auth_service.domain.entities.Permission;
import be.school.portal.auth_service.domain.entities.Role;
import be.school.portal.auth_service.domain.projections.UserProjection;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleProjectionMapper {

  @Mapping(
      source = "role.permissions",
      target = "permissions",
      qualifiedByName = "toPermissionNames")
  UserProjection.RoleProjection toRoleProjection(Role role);

  @Named("toPermissionNames")
  default Set<String> toPermissionNames(Set<Permission> permissions) {
    if (CollectionUtils.isEmpty(permissions)) {
      return Set.of();
    }
    return permissions.stream().map(Permission::getName).collect(Collectors.toSet());
  }
}
