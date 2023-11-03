package artgallery.hsserver.service;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.model.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * UserServiceTest
 */
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class UserServiceTest {
  @Autowired
  private UserService userService;

  @Test
  public void addUserRole() throws Exception {
    var userDTO = new UserDTO();
    var uniqueLogin = "user";
    var password = "testpwd";

    userDTO.setLogin(uniqueLogin);
    userDTO.setPassword(password);
    userService.register(userDTO);

    userService.addRole(uniqueLogin, Role.MODERATOR);
  }

}
