package be.school.portal.auth_service.domain.enums.converters;

import be.school.portal.auth_service.domain.enums.LoginAction;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoginActionConverter extends AbstractEnumConverter<LoginAction, Integer> {
  protected LoginActionConverter() {
    super(LoginAction.class);
  }
}
