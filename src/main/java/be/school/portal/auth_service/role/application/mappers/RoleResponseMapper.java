package be.school.portal.auth_service.role.application.mappers;

import be.school.portal.auth_service.common.dto.RoleResponse;
import be.school.portal.auth_service.role.domain.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleResponseMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  RoleResponse toRoleResponse(Role role);
  
}
