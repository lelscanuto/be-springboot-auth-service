package be.school.portal.auth_service.common.component;

import be.school.portal.auth_service.common.exceptions.InvalidCredentialException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

/**
 * Component responsible for validating and processing refresh tokens. Ensures the token is a valid
 * refresh token and extracts relevant claims.
 */
@Component
public class RefreshTokenProcessor {

  private final JwtTokenComponent jwtTokenComponent;

  /**
   * Constructs a new RefreshTokenProcessor.
   *
   * @param jwtTokenComponent the component used for JWT operations
   */
  public RefreshTokenProcessor(JwtTokenComponent jwtTokenComponent) {
    this.jwtTokenComponent = jwtTokenComponent;
  }

  /**
   * Validates that the given token is a well-formed and valid refresh token, and extracts the
   * username and JTI from it.
   *
   * @param token the JWT token to validate
   * @return a {@link RefreshTokenData} containing username and JTI
   * @throws InvalidCredentialException if the token is invalid or not a refresh token
   */
  @Nonnull
  public RefreshTokenData process(@Nonnull String token) {
    if (!jwtTokenComponent.validateToken(token)) {
      throw InvalidCredentialException.ofToken(token);
    }

    JwtTokenComponent.TokenType tokenType = jwtTokenComponent.getTokenType(token);
    if (JwtTokenComponent.TokenType.REFRESH != tokenType) {
      throw InvalidCredentialException.ofTokenType(tokenType.name());
    }

    final var username = jwtTokenComponent.getUsernameFromToken(token);
    final var jti = jwtTokenComponent.getJtiFromToken(token);

    return new RefreshTokenData(username, jti);
  }

  /**
   * Simple DTO to carry token claims extracted from a refresh token.
   *
   * @param username the username extracted from the token
   * @param jti the JWT ID extracted from the token
   */
  public record RefreshTokenData(String username, String jti) {}
}
