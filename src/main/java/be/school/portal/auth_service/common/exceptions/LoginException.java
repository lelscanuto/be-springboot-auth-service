package be.school.portal.auth_service.common.exceptions;

import java.io.Serial;

public abstract class LoginException extends RuntimeException {

  @Serial private static final long serialVersionUID = -6662533966145341891L;

  protected LoginException(String message) {
    super(message);
  }
}
