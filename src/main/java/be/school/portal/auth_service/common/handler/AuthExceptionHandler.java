package be.school.portal.auth_service.common.handler;

import be.school.portal.auth_service.common.exceptions.CodedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    var res = ProblemDetail.forStatus(errorCode.getHttpStatus());
    res.setTitle(errorCode.getCode());

    return ResponseEntity.status(errorCode.getHttpStatus()).body(res);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericExceptions(Exception exception) {

    LOGGER.error(exception.getMessage(), exception);

    return ResponseEntity.internalServerError()
        .body(ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
