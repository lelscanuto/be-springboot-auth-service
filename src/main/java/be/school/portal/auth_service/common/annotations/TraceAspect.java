package be.school.portal.auth_service.common.annotations;

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

  private static final Logger log = LoggerFactory.getLogger(TraceAspect.class);

  @Around("@within(trace)")
  public Object traceClassMethods(ProceedingJoinPoint pjp, Trace trace) throws Throwable {
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    String className = signature.getDeclaringType().getSimpleName();
    String methodName = signature.getName();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Object[] args = pjp.getArgs();
    String maskedArgs =
        trace.logArgsAndResult()
            ? "request=" + Arrays.stream(args).map(LogMasker::mask).toList()
            : "";

    Object result;

    try {
      result = pjp.proceed();

      if (result instanceof CompletableFuture<?> future) {
        // For async, log after completion without blocking
        future.whenComplete(
            (resolved, ex) -> {
              stopWatch.stop();

              String maskedResult = "";
              try {
                if (trace.logArgsAndResult() && resolved != null) {
                  maskedResult = ", " + LogMasker.mask(resolved);
                }
              } catch (Exception e) {
                maskedResult = ", <unable to mask>";
                log.warn("Failed to mask async result", e);
              }
              long execTime = stopWatch.getTotalTimeMillis();
              if (ex == null) {
                log.info(
                    "{}#{} (executionTime={}ms) {}{}",
                    className,
                    methodName,
                    execTime,
                    maskedArgs,
                    maskedResult);
              } else {
                log.error(
                    "{}#{} (executionTime={}ms) {}, threw exception: {}",
                    className,
                    methodName,
                    execTime,
                    maskedArgs,
                    ex.getMessage(),
                    ex);
              }
            });
      } else {
        stopWatch.stop();
        // Synchronous result
        String maskedResult = trace.logArgsAndResult() ? ", " + LogMasker.mask(result) : "";
        log.info(
            "{}#{} (executionTime={}ms) {}{}",
            className,
            methodName,
            stopWatch.getTotalTimeMillis(),
            maskedArgs,
            maskedResult);
      }

      return result;

    } catch (Throwable ex) {
      stopWatch.stop();
      log.error(
          "{}#{} (executionTime={}ms) {}, threw exception: {}",
          className,
          methodName,
          stopWatch.getTotalTimeMillis(),
          maskedArgs,
          ex.getMessage(),
          ex);
      throw ex;
    }
  }
}
