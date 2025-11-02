package be.school.portal.auth_service.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class CucumberSpringConfiguration {

  @Container
  @ServiceConnection(name = "redis", type = RedisConnectionDetails.class)
  public static GenericContainer<?> redis =
      new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine")).withExposedPorts(6379);

  @Container
  public static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15.1-alpine")
          .withUsername("postgres")
          .withPassword("postgres")
          .withDatabaseName("authentication");

  @DynamicPropertySource
  public static void initProperties(DynamicPropertyRegistry registry) {

    if (!postgres.isRunning()) postgres.start();
    if (!redis.isRunning()) redis.start();

    final String randomSchemaName = "test";

    registry.add("spring.flyway.schemas", () -> randomSchemaName);
    registry.add("spring.jpa.properties.hibernate.default.schema", () -> randomSchemaName);
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    registry.add("spring.data.redis.host", () -> redis.getHost());
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
  }
}
