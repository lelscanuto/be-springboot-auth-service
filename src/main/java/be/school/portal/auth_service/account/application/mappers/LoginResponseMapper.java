package be.school.portal.auth_service.account.application.mappers;

import be.school.portal.auth_service.common.dto.LoginResponse;
import be.school.portal.auth_service.account.application.internal.services.UserTokenCreationService;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoginResponseMapper {

  @Mapping(source = "userToken.accessToken", target = "accessToken")
  @Mapping(source = "userToken.refreshToken", target = "refreshToken")
  @Mapping(source = "userAccount.username", target = "username")
  @Mapping(source = "userAccount.roleNames", target = "roles")
  LoginResponse toLoginResponse(
      UserAccount userAccount, UserTokenCreationService.UserToken userToken);
}
