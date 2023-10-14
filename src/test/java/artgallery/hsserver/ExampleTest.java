package artgallery.hsserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.service.AuthService;

@ContextConfiguration
@SpringBootTest
public class ExampleTest {

  @Autowired
  AuthService authService;

  @Test
  public void addUser() throws RoleDoesNotExistException {
    var userDTO = new UserDTO();
    userDTO.setLogin("testlogin");
    userDTO.setPassword("testpwd");
    authService.register(userDTO);
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    TestConfiguration.setupRegistry(registry);
  }

}
