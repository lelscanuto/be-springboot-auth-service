package be.school.portal.auth_service.common.handler;

import be.school.portal.auth_service.common.exceptions.CodedException;
import be.school.portal.auth_service.common.exceptions.ErrorCode;
import be.school.portal.auth_service.common.utils.ZonedDateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthExceptionHandler.class);

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

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    final var problem = ProblemDetail.forStatus(errorCode.getHttpStatus());
    problem.setTitle(errorCode.getHttpStatus().getReasonPhrase());
    problem.setDetail("Something went wrong.");

    // ✅ Add custom fields
    problem.setProperty("code", errorCode.getCode());
    problem.setProperty("timestamp", ZonedDateTimeUtil.now());

    return ResponseEntity.internalServerError().body(problem);
  }
}
