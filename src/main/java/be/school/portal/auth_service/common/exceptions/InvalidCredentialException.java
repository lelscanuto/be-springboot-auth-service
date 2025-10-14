package be.school.portal.auth_service.common.exceptions;

import jakarta.annotation.Nonnull;
import java.io.Serial;

public class InvalidCredentialException extends LoginException implements CodedException {

  @Serial private static final long serialVersionUID = -6371429883775842405L;

  private static final String DEFAULT_MESSAGE = "Invalid credentials for user [%s].";
  private static final String TOKEN_MESSAGE = "Invalid or expired token: [%s].";
  private static final String TOKEN_TYPE_MESSAGE =
      "Token is not a refresh token, but a [%s] token.";

  private InvalidCredentialException(@Nonnull String message) {
    super(message);
  }

  public static InvalidCredentialException ofUsername(@Nonnull String username) {
    return new InvalidCredentialException(String.format(username, DEFAULT_MESSAGE));
  }

  public static InvalidCredentialException ofToken(@Nonnull String token) {
    return new InvalidCredentialException(String.format(TOKEN_MESSAGE, token));
  }

  public static InvalidCredentialException ofTokenType(String tokenName) {
    return new InvalidCredentialException(String.format(TOKEN_TYPE_MESSAGE, tokenName));
  }

  @Nonnull
  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.USER_INVALID_CREDENTIAL;
  }
}
