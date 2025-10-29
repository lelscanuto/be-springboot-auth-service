package be.school.portal.auth_service.common.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trace {
  boolean logResult() default false; // optional: log returned result
}
