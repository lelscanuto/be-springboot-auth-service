package be.school.portal.auth_service.role.application.mappers;

import be.school.portal.auth_service.common.dto.PermissionResponse;
import be.school.portal.auth_service.common.dto.RolePermissionResponse;
import be.school.portal.auth_service.permission.application.mappers.PermissionResponseMapper;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import be.school.portal.auth_service.role.application.dto.RolePageablePermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    imports = PermissionResponseMapper.class)
public abstract class RolePermissionMapper {

  @Autowired private PermissionResponseMapper permissionResponseMapper;

  @Mapping(
      source = "rolePageablePermissionDTO.permissionPageable",
      target = "permissions",
      qualifiedByName = "toPermissionDTOPage")
  @Mapping(source = "rolePageablePermissionDTO.roleId", target = "roleId")
  public abstract RolePermissionResponse toRolePermissionResponse(
      RolePageablePermissionDTO rolePageablePermissionDTO);

  @Named("toPermissionDTOPage")
  protected Page<PermissionResponse> toPermissionDTO(Page<Permission> permission) {
    return permission.map(permissionResponseMapper::toPermissionResponse);
  }
}
