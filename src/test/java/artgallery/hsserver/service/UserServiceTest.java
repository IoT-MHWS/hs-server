package artgallery.hsserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.exception.UserRoleAlreadyExists;
import artgallery.hsserver.model.Role;
import jakarta.transaction.Transactional;

/**
 * UserServiceTest
 */
@SpringBootTest
@ExtendWith({ TestExtension.class })
@Transactional
public class UserServiceTest {

  @Autowired
  private AuthService authService;

  @Autowired
  private UserService userService;

  @Test
  public void addUserRole() throws RoleDoesNotExistException, UserDoesNotExistException, UserRoleAlreadyExists {
    var userDTO = new UserDTO();
    var uniqueLogin = "user";
    var password = "testpwd";

    userDTO.setLogin(uniqueLogin);
    userDTO.setPassword(password);
    authService.register(userDTO);

    userService.addRole(uniqueLogin, Role.MODERATOR);
  }

}
