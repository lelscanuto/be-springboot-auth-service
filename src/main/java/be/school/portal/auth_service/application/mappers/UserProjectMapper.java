package be.school.portal.auth_service.application.mappers;

import be.school.portal.auth_service.domain.entities.UserAccount;
import be.school.portal.auth_service.domain.projections.UserLiteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProjectMapper {

  UserLiteDTO toUserLiteDTO(UserAccount userAccount);
}
