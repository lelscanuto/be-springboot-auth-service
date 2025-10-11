package be.school.portal.auth_service.application.mappers;

import be.school.portal.auth_service.application.dto.LoginResponse;
import be.school.portal.auth_service.domain.entities.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoginResponseMapper {

  @Mapping(source = "token", target = "token")
  @Mapping(source = "userAccount.username", target = "username")
  @Mapping(source = "userAccount.roleNames", target = "roles")
  LoginResponse toLoginResponse(UserAccount userAccount, String token);
}
