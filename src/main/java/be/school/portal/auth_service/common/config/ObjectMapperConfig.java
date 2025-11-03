package be.school.portal.auth_service.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Centralized configuration for Jackson's {@link ObjectMapper}.
 *
 * <p>This configuration defines a lenient {@code ObjectMapper} bean used across the application for
 * flexible JSON serialization and deserialization. It disables strict validation on unknown JSON
 * properties and registers time-handling modules for improved Java Time API support.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Disables {@link
 *       com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} to allow
 *       forward-compatible JSON parsing.
 *   <li>Registers {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} for Java 8+
 *       date/time type support.
 *   <li>Marks this mapper as {@link org.springframework.context.annotation.Primary}, making it the
 *       default {@code ObjectMapper} for dependency injection.
 * </ul>
 *
 * <p>This configuration ensures consistency across all JSON (de)serialization operations,
 * particularly for REST APIs, event payloads, and domain object mapping.
 *
 * @author Francis Jorell Canuto
 */
@Configuration
public class ObjectMapperConfig {

  /**
   * Creates and configures a lenient {@link ObjectMapper} instance.
   *
   * <p>The configured mapper:
   *
   * <ul>
   *   <li>Ignores unknown JSON fields during deserialization.
   *   <li>Supports {@code java.time} API types such as {@code LocalDateTime} and {@code
   *       ZonedDateTime}.
   *   <li>Is designated as the primary {@code ObjectMapper} bean.
   * </ul>
   *
   * @param builder the Jackson builder provided by Spring Boot auto-configuration
   * @return a fully configured lenient {@link ObjectMapper} bean
   */
  @Bean("lenientObjectMapper")
  @Primary
  public ObjectMapper lenientObjectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper objectMapper = builder.createXmlMapper(false).build();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
}
