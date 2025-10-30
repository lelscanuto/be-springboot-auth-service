package be.school.portal.auth_service.common.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Hide {
  MaskStrategy value() default MaskStrategy.MASK; // you can mark multiple types

  enum MaskStrategy {
    MASK, // Replace with asterisks
    OMIT, // Don’t log at all
    HASH // Hash before logging
  }
}
