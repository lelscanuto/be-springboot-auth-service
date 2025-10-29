package be.school.portal.auth_service.account.application.mappers;

import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.domain.projections.UserProjection;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RoleProjectionMapper.class)
public interface UserProjectionMapper {

  UserProjection toUserLiteDTO(UserAccount userAccount);
}
