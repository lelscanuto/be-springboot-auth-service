package be.school.portal.auth_service.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class JtiUtil {

  private JtiUtil() {}

  /**
   * Generates a JTI from a refresh token ID and secret.
   *
   * @param id the refresh token database ID
   * @param salt a secret salt
   * @return the encoded JTI
   */
  public static String createJti(long id, String salt) {
    String value = id + ":" + salt; // combine id and secret
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Decodes a JTI to retrieve the refresh token ID.
   *
   * @param jti the encoded JTI
   * @param salt the secret salt (must match the one used to encode)
   * @return the refresh token ID
   * @throws IllegalArgumentException if the JTI is invalid
   */
  public static long decodeJti(String jti, String salt) {
    String decoded = new String(Base64.getUrlDecoder().decode(jti), StandardCharsets.UTF_8);
    String[] parts = decoded.split(":");
    if (parts.length != 2 || !parts[1].equals(salt)) {
      throw new IllegalArgumentException("Invalid JTI");
    }
    return Long.parseLong(parts[0]);
  }
}
