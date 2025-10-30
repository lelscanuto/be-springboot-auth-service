package be.school.portal.auth_service.common.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trace {
  boolean logArgsAndResult() default false;
}
