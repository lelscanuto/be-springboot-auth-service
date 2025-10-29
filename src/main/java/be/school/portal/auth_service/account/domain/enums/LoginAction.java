package be.school.portal.auth_service.account.domain.enums;

import be.school.portal.auth_service.account.domain.enums.converters.ConvertibleEnum;

public enum LoginAction implements ConvertibleEnum<Integer> {
  LOGIN_SUCCESS(10),
  LOGIN_FAILED_INVALID_CREDENTIAL(-11),
  LOGIN_FAILED_INVALID_STATE(-12),
  LOGOUT(20),
  SESSION_EXPIRED(30);

  private final int value;

  LoginAction(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return this.value;
  }
}
