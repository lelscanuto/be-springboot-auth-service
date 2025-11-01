package be.school.portal.auth_service.permission.application.mappers;

import be.school.portal.auth_service.common.dto.PermissionResponse;
import be.school.portal.auth_service.permission.domain.entities.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionResponseMapper {
  PermissionResponse toPermissionResponse(Permission permission);
}
