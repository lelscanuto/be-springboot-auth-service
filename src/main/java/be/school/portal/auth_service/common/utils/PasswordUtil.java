package be.school.portal.auth_service.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Utility class for password hashing and verification using BCrypt. */
public final class PasswordUtil {

  private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

  // Private constructor to prevent instantiation
  private PasswordUtil() {
    throw new UnsupportedOperationException("PasswordUtil cannot be instantiated");
  }

  /**
   * Hash a raw password using BCrypt.
   *
   * @param rawPassword plain text password
   * @return hashed password
   */
  public static String hash(String rawPassword) {
    return ENCODER.encode(rawPassword);
  }

  /**
   * Verify a raw password against a hashed password.
   *
   * @param rawPassword plain text password
   * @param hashedPassword previously hashed password
   * @return true if matches, false otherwise
   */
  public static boolean verify(String rawPassword, String hashedPassword) {
    return ENCODER.matches(rawPassword, hashedPassword);
  }
}
