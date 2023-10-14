package artgallery.hsserver;

import java.io.File;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class TestExtension implements BeforeAllCallback, CloseableResource {

  private static final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LoggerFactory.getLogger(TestExtension.class));

  private static final DockerComposeContainer<?> dockerComposeContainer = new DockerComposeContainer<>(
      new File("docker-compose.yml"))
      .withExposedService("postgres", 5432);

  @Override
  public void beforeAll(ExtensionContext context) {
    dockerComposeContainer.withLogConsumer("liquibase", logConsumer);
    dockerComposeContainer.start();
  }

  @Override
  public void close() {
    dockerComposeContainer.close();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    String postgresHost = dockerComposeContainer.getServiceHost("postgres", 5432);
    Integer postgresPort = dockerComposeContainer.getServicePort("postgres", 5432);
    registry.add("spring.datasource.url",
        () -> String.format("jdbc:postgresql://%s:%d/art", postgresHost, postgresPort));
  }

}
