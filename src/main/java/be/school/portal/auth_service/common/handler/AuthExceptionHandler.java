package be.school.portal.auth_service.common.handler;

import be.school.portal.auth_service.common.builders.ProblemDetailFactory;
import be.school.portal.auth_service.common.exceptions.CodedException;
import be.school.portal.auth_service.common.utils.ZonedDateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthExceptionHandler.class);

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDeniedException(
      AccessDeniedException badCredentialsException) {

    LOGGER.error(badCredentialsException.getMessage(), badCredentialsException);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ProblemDetailFactory.forbidden());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthException(AuthenticationException exception) {

    LOGGER.error(exception.getMessage(), exception);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ProblemDetailFactory.unauthorized());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ProblemDetail> handleCodedExceptions(CodedException exception) {

    final var errorCode = exception.getErrorCode();

    final var problem = ProblemDetail.forStatus(errorCode.getHttpStatus());
    problem.setTitle(errorCode.getHttpStatus().getReasonPhrase());
    problem.setDetail("Something went wrong.");

    // ✅ Add custom fields
    problem.setProperty("code", errorCode.getCode());
    problem.setProperty("timestamp", ZonedDateTimeUtil.now());

    RuntimeException runtimeException = (RuntimeException) exception;

    LOGGER.error(runtimeException.getMessage(), runtimeException);

    return ResponseEntity.status(errorCode.getHttpStatus()).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericExceptions(Exception exception) {

    LOGGER.error(exception.getMessage(), exception);

    final var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    problem.setDetail("Something went wrong.");

    // ✅ Add custom fields
    problem.setProperty("code", HttpStatus.INTERNAL_SERVER_ERROR.name());
    problem.setProperty("timestamp", ZonedDateTimeUtil.now());

    return ResponseEntity.internalServerError().body(problem);
  }
}
