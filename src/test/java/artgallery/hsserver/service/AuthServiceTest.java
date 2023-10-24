package artgallery.hsserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.UserDTO;
import jakarta.transaction.Transactional;

@SpringBootTest
@ExtendWith({ TestExtension.class })
@Transactional
public class AuthServiceTest {

  @Autowired
  AuthService authService;

  @Test
  public void addUser() throws Exception {
    var userDTO = new UserDTO();
    var uniqueLogin = "user";
    userDTO.setLogin(uniqueLogin);
    userDTO.setPassword("testpwd");
    authService.register(userDTO);
  }

}
