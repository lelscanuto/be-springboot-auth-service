package be.school.portal.auth_service.account.application.mappers;

import be.school.portal.auth_service.account.application.dto.UserLoginDTO;
import be.school.portal.auth_service.common.dto.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoginResponseMapper {

  LoginResponse toLoginResponse(UserLoginDTO userLoginDTO);
}
