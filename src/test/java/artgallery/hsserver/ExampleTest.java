package artgallery.hsserver;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.service.AuthService;

@ContextConfiguration
@SpringBootTest
public class ExampleTest {

  public static Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LoggerFactory.getLogger(ExampleTest.class));

  public static DockerComposeContainer<?> dockerComposeContainer = new DockerComposeContainer<>(
      new File("docker-compose.yml"))
      .withExposedService("postgres", 5432);

  @BeforeAll
  public static void start() {
    dockerComposeContainer.withLogConsumer("liquibase", logConsumer);
    dockerComposeContainer.start();
  }

  @AfterAll
  public static void close() {
    dockerComposeContainer.close();
  }

  @Autowired
  AuthService authService;

  @Test
  public void addUser() throws RoleDoesNotExistException, InterruptedException {
    var userDTO = new UserDTO();
    userDTO.setLogin("testlogin");
    userDTO.setPassword("testpwd");
    authService.register(userDTO);
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    String postgresHost = dockerComposeContainer.getServiceHost("postgres", 5432);
    Integer postgresPort = dockerComposeContainer.getServicePort("postgres", 5432);
    registry.add("spring.datasource.url",
        () -> String.format("jdbc:postgresql://%s:%d/art", postgresHost, postgresPort));
  }

}
