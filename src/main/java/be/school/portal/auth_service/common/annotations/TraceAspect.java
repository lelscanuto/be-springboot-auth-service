package be.school.portal.auth_service.common.annotations;

import static net.logstash.logback.argument.StructuredArguments.kv;

import be.school.portal.auth_service.common.logging.LoggerName;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TraceAspect {

  private static final String REPOSITORY_LOGGER = "repository-logger";
  private static final String ADAPTER_LOGGER = "adapter-logger";
  private static final String DEFAULT_LOGGER = "root";

  private static final String EXECUTION_TIME = "executionTime";
  private static final String METHOD_NAME = "methodName";
  private static final String IS_SUCCESS = "isSuccess";

  private static String getLoggerName(LoggerName logger) {
    return switch (logger) {
      case REPOSITORY_LOGGER -> REPOSITORY_LOGGER;
      case ADAPTER_LOGGER -> ADAPTER_LOGGER;
      case DEFAULT -> DEFAULT_LOGGER;
    };
  }

  private static void logAsync(
      Trace trace,
      CompletableFuture<?> future,
      StopWatch stopWatch,
      Logger log,
      String methodName,
      String maskedArgs) {
    // For async, log after completion without blocking
    future.whenComplete(
        (resolved, ex) -> {

          // Stop the stopwatch when the future is completed
          stopWatch.stop();

          String maskedResult = "";

          // Mask the result if needed
          if (isLogRequestAndResult(trace) && resolved != null) {

            try {
              // Masking the result might throw an exception
              maskedResult = ", " + LogMasker.mask(resolved);
            } catch (Exception e) {
              // If masking fails, log a warning and use a placeholder
              maskedResult = ", <unable to mask>";
              log.warn("Failed to mask async result", e);
            }
          }

          // Log the result or exception
          long execTime = stopWatch.getTotalTimeMillis();
          boolean isSuccess = ex == null;

          if (isSuccess) {
            log.info(
                "[{}ms, {}, {}] {}{}",
                kv(EXECUTION_TIME, execTime),
                kv(METHOD_NAME, methodName),
                kv(IS_SUCCESS, false),
                maskedArgs,
                maskedResult);
          } else {
            log.error(
                "[{}ms, {}, {}] {}, threw exception: {}",
                kv(EXECUTION_TIME, execTime),
                kv(METHOD_NAME, methodName),
                kv(IS_SUCCESS, false),
                maskedArgs,
                ex.getMessage(),
                ex);
          }
        });
  }

  private static void logSync(
      Trace trace,
      StopWatch stopWatch,
      Object result,
      Logger log,
      String methodName,
      String maskedArgs) {

    // Stop the stopwatch for synchronous methods
    stopWatch.stop();

    // Synchronous result
    String maskedResult = isLogRequestAndResult(trace) ? ", " + LogMasker.mask(result) : "";

    // Get execution time
    long execTime = stopWatch.getTotalTimeMillis();

    log.info(
        "[{}ms, {}, {}] {}{}",
        kv(EXECUTION_TIME, execTime),
        kv(METHOD_NAME, methodName),
        kv(IS_SUCCESS, false),
        maskedArgs,
        maskedResult);
  }

  // Determine if we should log both request and result
  private static boolean isLogRequestAndResult(Trace log) {
    return log.logger() == LoggerName.ADAPTER_LOGGER;
  }

  @Around("@within(trace)")
  public Object traceClassMethods(ProceedingJoinPoint pjp, Trace trace) throws Throwable {

    String loggerName = getLoggerName(trace.logger());
    org.slf4j.Logger log = LoggerFactory.getLogger(loggerName);

    MethodSignature signature = (MethodSignature) pjp.getSignature();
    String className = signature.getDeclaringType().getSimpleName();
    String signatureName = signature.getName();
    String methodName = String.format("%s#%s", className, signatureName);

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Object[] args = pjp.getArgs();
    String maskedArgs =
        isLogRequestAndResult(trace)
            ? "request=" + Arrays.stream(args).map(LogMasker::mask).toList()
            : "";

    Object result;

    try {
      result = pjp.proceed();

      if (result instanceof CompletableFuture<?> future) {
        logAsync(trace, future, stopWatch, log, methodName, maskedArgs);
      } else {
        logSync(trace, stopWatch, result, log, methodName, maskedArgs);
      }

      return result;

    } catch (Throwable ex) {

      // On exception, stop the stopwatch and log the error
      stopWatch.stop();

      // Log the result or exception
      long execTime = stopWatch.getTotalTimeMillis();

      log.error(
          "[{}ms, {}, {}] {}, threw exception: {}",
          kv(EXECUTION_TIME, execTime),
          kv(METHOD_NAME, methodName),
          kv(IS_SUCCESS, false),
          maskedArgs,
          ex.getMessage(),
          ex);

      throw ex;
    }
  }
}
