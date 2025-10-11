package be.school.portal.auth_service.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI authServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Authentication Service API")
                .description("Endpoints for user authentication, login, and token management.")
                .version("v1.0")
                .contact(
                    new Contact()
                        .name("School Portal Dev Team")
                        .email("support@school-portal.local"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}
