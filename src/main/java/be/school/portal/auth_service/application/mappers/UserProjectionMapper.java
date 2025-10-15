package be.school.portal.auth_service.application.mappers;

import be.school.portal.auth_service.domain.entities.UserAccount;
import be.school.portal.auth_service.domain.projections.UserProjection;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RoleProjectionMapper.class)
public interface UserProjectionMapper {

  UserProjection toUserLiteDTO(UserAccount userAccount);
}
