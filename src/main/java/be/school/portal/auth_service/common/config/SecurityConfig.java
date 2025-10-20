package be.school.portal.auth_service.common.config;

import be.school.portal.auth_service.common.exceptions.ErrorCode;
import be.school.portal.auth_service.common.handler.JwtAuthenticationFilter;
import be.school.portal.auth_service.common.utils.ZonedDateTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final ObjectMapper objectMapper;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.objectMapper = objectMapper;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                // Public auth endpoints
                auth.requestMatchers("/api/auth/login", "/api/auth/token/refresh")
                    .permitAll()
                    // Swagger endpoints
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          ErrorCode unauthorize = ErrorCode.USER_UNAUTHORIZED;
                          var problem = ProblemDetail.forStatus(unauthorize.getHttpStatus());
                          problem.setTitle("Unauthorized");
                          problem.setDetail("Invalid or missing authentication token.");
                          problem.setInstance(URI.create(request.getRequestURI()));

                          // ✅ Add custom fields
                          problem.setProperty("code", unauthorize.getCode());
                          problem.setProperty("timestamp", ZonedDateTimeUtil.now());

                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/problem+json");
                          objectMapper.writeValue(response.getOutputStream(), problem);
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          ErrorCode forbidden = ErrorCode.USER_FORBIDDEN;
                          var problem = ProblemDetail.forStatus(forbidden.getHttpStatus());
                          problem.setTitle("Forbidden");
                          problem.setDetail("You do not have permission to access this resource.");
                          problem.setInstance(URI.create(request.getRequestURI()));

                          // ✅ Add custom fields
                          problem.setProperty("code", forbidden.getCode());
                          problem.setProperty("timestamp", ZonedDateTimeUtil.now());

                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/problem+json");
                          objectMapper.writeValue(response.getOutputStream(), problem);
                        }))
        .build();
  }
}
