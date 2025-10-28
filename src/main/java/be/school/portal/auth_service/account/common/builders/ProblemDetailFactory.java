package be.school.portal.auth_service.account.common.builders;

import be.school.portal.auth_service.account.common.utils.ZonedDateTimeUtil;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public final class ProblemDetailFactory {

  private ProblemDetailFactory() {
    throw new UnsupportedOperationException("ProblemDetailFactory cannot be instantiated");
  }

  public static ProblemDetail unauthorized() {
    var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

    problem.setTitle("Unauthorized");
    problem.setDetail("Invalid or missing authentication token.");

    // ✅ Add custom fields
    problem.setProperty("code", "UNAUTHORIZED");
    problem.setProperty("timestamp", ZonedDateTimeUtil.now());

    return problem;
  }

  public static ProblemDetail unauthorized(String requestUri) {

    final var problemDetail = unauthorized();

    problemDetail.setInstance(URI.create(requestUri));

    return problemDetail;
  }

  public static ProblemDetail forbidden(String requestUri) {

    final var problemDetail = forbidden();

    problemDetail.setInstance(URI.create(requestUri));

    return problemDetail;
  }

  public static ProblemDetail forbidden() {
    var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

    problem.setTitle("Forbidden");
    problem.setDetail("Invalid or missing authentication token.");

    // ✅ Add custom fields
    problem.setProperty("code", "FORBIDDEN");
    problem.setProperty("timestamp", ZonedDateTimeUtil.now());

    return problem;
  }
}
