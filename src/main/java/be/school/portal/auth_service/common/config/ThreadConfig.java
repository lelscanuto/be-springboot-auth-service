package be.school.portal.auth_service.common.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.MDC;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

@Configuration
@EnableAsync
public class ThreadConfig {
  @Bean
  public AsyncTaskExecutor applicationTaskExecutor(TaskDecorator taskDecorator) {
    ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    // Wrap virtual threads with SecurityContext propagation
    DelegatingSecurityContextExecutorService securityExecutor =
        new DelegatingSecurityContextExecutorService(virtualExecutor);
    // Wrap with Spring's AsyncTaskExecutor adapter + TaskDecorator for MDC
    TaskExecutorAdapter adapter = new TaskExecutorAdapter(securityExecutor);
    adapter.setTaskDecorator(taskDecorator);
    return adapter;
  }

  @Bean
  public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return protocolHandler -> {
      protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    };
  }

  @Bean
  TaskDecorator taskDecorator() {
    return new ContextPropagatingTaskDecorator() {
      @Override
      public Runnable decorate(Runnable runnable) {
        return super.decorate(
            () -> {
              try {
                runnable.run();
              } finally {
                MDC.clear();
              }
            });
      }
    };
  }
}
