package artgallery.hsserver.controller;

import artgallery.hsserver.dto.TokenDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.repository.UserRepository;
import artgallery.hsserver.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public abstract class AuthorizedControllerTest {
  protected static String username = "user";
  protected static String password = "password";

  static protected final ObjectMapper objectMapper = new ObjectMapper();

  protected static TokenDTO tokenDTO;

  @BeforeAll
  static public void authorizeUser(@Autowired UserRepository userRepository, @Autowired AuthService authService) throws Exception {
    userRepository.deleteAll();

    UserDTO userDTO = new UserDTO();
    userDTO.setLogin(username);
    userDTO.setPassword(password);
    authService.register(userDTO);

    tokenDTO = authService.login(userDTO);
  }

  @AfterAll
  static public void cleanup(@Autowired UserRepository userRepository) {
    userRepository.deleteAll();
  }
}
