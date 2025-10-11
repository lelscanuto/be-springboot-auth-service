package be.school.portal.auth_service.common.utils;

import jakarta.annotation.Nonnull;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class ZonedDateTimeUtil {

  private ZonedDateTimeUtil() {
    throw new UnsupportedOperationException("ZoneDateTimeUtil cannot be instantiated");
  }

  /** Returns current UTC time */
  public static ZonedDateTime now() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }

  public static ZonedDateTime plusMillis(@Nonnull ZonedDateTime now, @Nonnull Long milliSeconds) {
    return now.plusNanos(milliSeconds * 1_000_000);
  }
}
