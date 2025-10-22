package be.school.portal.auth_service.common.config;

import be.school.portal.auth_service.application.providers.JwtAuthenticationProvider;
import be.school.portal.auth_service.common.builders.ProblemDetailFactory;
import be.school.portal.auth_service.common.handler.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final ObjectMapper objectMapper;
  private final JwtAuthenticationProvider jwtAuthenticationProvider;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      ObjectMapper objectMapper,
      JwtAuthenticationProvider jwtAuthenticationProvider) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.objectMapper = objectMapper;
    this.jwtAuthenticationProvider = jwtAuthenticationProvider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            auth ->

                // Public auth endpoints
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/api/auth/login", "/api/auth/token/refresh")
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
        .authenticationProvider(jwtAuthenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          LOGGER.error(authException.getMessage(), authException);

                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/problem+json");

                          objectMapper.writeValue(
                              response.getOutputStream(),
                              ProblemDetailFactory.unauthorized(request.getRequestURI()));
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          LOGGER.error(accessDeniedException.getMessage(), accessDeniedException);

                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/problem+json");

                          objectMapper.writeValue(
                              response.getOutputStream(),
                              ProblemDetailFactory.forbidden(request.getRequestURI()));
                        }))
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    // Get the default AuthenticationManager from Spring Security
    return authConfig.getAuthenticationManager();
  }
}
