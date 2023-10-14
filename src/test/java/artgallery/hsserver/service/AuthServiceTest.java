package artgallery.hsserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.util.StringModifier;

@SpringBootTest
@ExtendWith({ TestExtension.class })
public class AuthServiceTest {

  @Autowired
  AuthService authService;

  @Test
  public void addUser() throws RoleDoesNotExistException, InterruptedException {
    var userDTO = new UserDTO();
    var uniqueLogin = StringModifier.getUniqueString("user");
    userDTO.setLogin(uniqueLogin);
    userDTO.setPassword("testpwd");
    authService.register(userDTO);
  }

}
