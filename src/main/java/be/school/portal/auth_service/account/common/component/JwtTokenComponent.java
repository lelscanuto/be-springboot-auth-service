package be.school.portal.auth_service.account.common.component;

import be.school.portal.auth_service.account.common.utils.ZonedDateTimeUtil;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.role.domain.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class JwtTokenComponent {

  private final Key signingKey;

  public JwtTokenComponent(@Value("${jwt.secret}") String secret) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(
      JwtUserDetails jwtUserDetails, long expirationMs, TokenType tokenType) {
    return generateToken(jwtUserDetails, expirationMs, tokenType, null);
  }

  public String generateToken(
      JwtUserDetails jwtUserDetails, long expirationMs, TokenType tokenType, String jti) {
    ZonedDateTime now = ZonedDateTime.now();
    Date issuedAt = Date.from(now.toInstant());
    Date expiryDate = Date.from(ZonedDateTimeUtil.plusMillis(now, expirationMs).toInstant());

    var builder =
        Jwts.builder()
            .setSubject(jwtUserDetails.username())
            .setIssuedAt(issuedAt)
            .setExpiration(expiryDate);

    final var userRoles = jwtUserDetails.roles();

    if (!CollectionUtils.isEmpty(userRoles)) {
      builder.claim("roles", userRoles);
    }

    if (StringUtils.isNotBlank(jti)) {
      builder.setId(jti);
    }

    builder.claim("type", tokenType.name().toLowerCase());

    return builder.signWith(signingKey, SignatureAlgorithm.HS256).compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      Claims claims = getClaims(token);
      String username = claims.getSubject();
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /** Validate token signature and expiration */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /** Extract username claim (requires same signing key for parsing) */
  public String getUsernameFromToken(String token) {
    return getClaims(token).getSubject();
  }

  /** Extract roles claim */
  public Set<String> getRolesFromToken(String token) {
    Object rolesObj = getClaims(token).get("roles");
    if (rolesObj instanceof List<?> rolesList) {
      return rolesList.stream().map(Object::toString).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public TokenType getTokenType(String token) {
    String tokenTypeName = getClaims(token).get("type", String.class);
    try {
      return TokenType.valueOf(tokenTypeName.toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid or missing token type", e);
    }
  }

  public String getJtiFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getId();
  }

  private Claims getClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
  }

  private boolean isTokenExpired(String token) {
    Date expiration = getClaims(token).getExpiration();
    ZonedDateTime expirationTime =
        ZonedDateTime.ofInstant(expiration.toInstant(), ZoneId.of("UTC"));
    return ZonedDateTimeUtil.now().isAfter(expirationTime);
  }

  private Set<String> extractRoles(UserAccount userAccount) {
    if (userAccount.getRoles() == null) return Set.of();
    return userAccount.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
  }

  public enum TokenType {
    ACCESS,
    REFRESH
  }
}
