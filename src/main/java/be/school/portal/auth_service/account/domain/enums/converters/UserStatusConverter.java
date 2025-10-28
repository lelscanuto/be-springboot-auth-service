package be.school.portal.auth_service.account.domain.enums.converters;

import be.school.portal.auth_service.account.domain.enums.UserStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter extends AbstractEnumConverter<UserStatus, Integer> {
  protected UserStatusConverter() {
    super(UserStatus.class);
  }
}
