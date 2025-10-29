package be.school.portal.auth_service.role.application.mappers;

import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.domain.entities.Role;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleResponseMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "permissions", target = "permissions", qualifiedByName = "toPermissionName")
  RoleResponse toRoleResponse(Role role);

  @Named("toPermissionName")
  default Set<String> toPermissionName(Set<Permission> permissions) {
    if (CollectionUtils.isEmpty(permissions)) {
      return Set.of();
    }
    return permissions.stream().map(Permission::getName).collect(Collectors.toSet());
  }
}
