package be.school.portal.auth_service.account.application.mappers;

import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.domain.projections.UserProjection;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.domain.entities.Role;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProjectionMapper {

  @Mapping(source = "userAccount.roles", target = "roles", qualifiedByName = "toRoleNames")
  @Mapping(
      source = "userAccount.roles",
      target = "permissions",
      qualifiedByName = "toRolePermissionsNames")
  UserProjection toUserLiteDTO(UserAccount userAccount);

  @Named("toRolePermissionsNames")
  default Set<String> toRolePermissionsNames(Set<Role> roles) {

    if (CollectionUtils.isEmpty(roles)) {
      return Set.of();
    }

    return roles.stream()
        .map(Role::getPermissions)
        .flatMap(Collection::stream)
        .map(Permission::getName)
        .collect(Collectors.toSet());
  }

  @Named("toRoleNames")
  default Set<String> toRoleNames(Set<Role> roles) {

    if (CollectionUtils.isEmpty(roles)) {
      return Set.of();
    }

    return roles.stream().map(Role::getName).collect(Collectors.toSet());
  }
}
