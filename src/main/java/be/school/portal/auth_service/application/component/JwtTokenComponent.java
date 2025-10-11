package be.school.portal.auth_service.application.component;

import be.school.portal.auth_service.common.utils.ZonedDateTimeUtil;
import be.school.portal.auth_service.domain.entities.Role;
import be.school.portal.auth_service.domain.entities.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/** Component for generating, parsing, and validating JWT tokens. */
@Component
public class JwtTokenComponent {

  private final Key signingKey;
  private final long expirationMs;

  /**
   * Constructs a JwtTokenComponent with the provided secret and expiration.
   *
   * @param secret the secret key for signing the JWT
   * @param expirationMs the expiration time in milliseconds
   */
  public JwtTokenComponent(
      @Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationMs) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  /**
   * Generates a JWT token for the given user.
   *
   * @param userAccount the user for whom the token is generated
   * @return the generated JWT token as a String
   */
  public String generateToken(UserAccount userAccount) {
    ZonedDateTime now = ZonedDateTimeUtil.now();
    Date issuedAt = Date.from(now.toInstant());
    Date expiryDate = Date.from(ZonedDateTimeUtil.plusMillis(now, expirationMs).toInstant());

    Set<String> roles =
        !CollectionUtils.isEmpty(userAccount.getRoles())
            ? userAccount.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
            : Set.of();

    return Jwts.builder()
        .setSubject(userAccount.getUsername())
        .claim("roles", roles)
        .setIssuedAt(issuedAt)
        .setExpiration(expiryDate)
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts the username (subject) from the given JWT token.
   *
   * @param token the JWT token
   * @return the username extracted from the token
   */
  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /**
   * Extracts the roles from the given JWT token.
   *
   * @param token the JWT token
   * @return a set of role names extracted from the token
   */
  public Set<String> getRolesFromToken(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();

    Object rolesObj = claims.get("roles");

    if (rolesObj instanceof List<?> rolesList) {
      return rolesList.stream().map(Object::toString).collect(Collectors.toSet());
    }

    return Set.of();
  }

  /**
   * Validates the given JWT token.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
