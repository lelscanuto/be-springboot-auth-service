package be.school.portal.auth_service.common.annotations;

import be.school.portal.auth_service.common.logging.LoggerName;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trace {
  LoggerName logger() default LoggerName.DEFAULT;
}
