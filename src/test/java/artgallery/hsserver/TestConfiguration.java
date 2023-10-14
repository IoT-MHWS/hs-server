package artgallery.hsserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/**
 * AbstractIntegrationTest
 */
public class TestConfiguration {

  public static Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LoggerFactory.getLogger(TestConfiguration.class));

  private static final String propertiesResource = "test.properties";

  public final static Properties testProperties;

  public static DockerComposeContainer<?> dockerComposeContainer = new DockerComposeContainer<>(
      new File("src/test/resources/test-docker-compose.yml"));

  static {
    testProperties = new Properties();
    try {
      // load a properties file from class path, inside static method
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      InputStream stream = loader.getResourceAsStream(propertiesResource);
      testProperties.load(stream);

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    dockerComposeContainer.withLogConsumer("liquibase", logConsumer);
  }

  public static void setupRegistry(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> testProperties.getProperty("postgres.url"));
    registry.add("spring.datasource.username", () -> testProperties.getProperty("postgres.username"));
    registry.add("spring.datasource.password", () -> testProperties.getProperty("postgres.password"));
  }

}
