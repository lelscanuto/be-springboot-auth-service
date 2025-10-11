package be.school.portal.auth_service.domain.enums;

import be.school.portal.auth_service.domain.enums.converters.ConvertibleEnum;

public enum UserStatus implements ConvertibleEnum<Integer> {
  ACTIVE(1),
  INACTIVE(0),
  LOCKED(-1);

  private final int value;

  UserStatus(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return this.value;
  }
}
