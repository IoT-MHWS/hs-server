package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.TokenDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.repository.UserRepository;
import artgallery.hsserver.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  AuthService authService;

  static private final ObjectMapper objectMapper = new ObjectMapper();

  static private UserDTO userDTO;

  @BeforeAll
  static void createArtistDTO() {
    userDTO = new UserDTO();
    userDTO.setLogin("user");
    userDTO.setPassword("user");
  }

  @Test
  public void testRegisterUser() throws Exception {
    String request = objectMapper.writeValueAsString(userDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
        .content(request)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    assertAll(
      () -> assertEquals(201, response.getStatus()),
      () -> assertEquals("ok", response.getContentAsString())
    );
  }

  @Test
  public void testLoginUser() throws Exception {
    authService.register(userDTO);

    String request = objectMapper.writeValueAsString(userDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
        .content(request)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    TokenDTO resultDTO = objectMapper.readValue(response.getContentAsString(), TokenDTO.class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertNotNull(resultDTO.getJwtToken()),
      () -> assertNotNull(resultDTO.getRefreshToken())
    );
  }

  @Test
  public void testTokenRefresh() throws Exception {
    authService.register(userDTO);
    TokenDTO tokenDTO = authService.login(userDTO);

    String request = objectMapper.writeValueAsString(userDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getRefreshToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    TokenDTO resultDTO = objectMapper.readValue(response.getContentAsString(), TokenDTO.class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertNotNull(resultDTO.getJwtToken()),
      () -> assertNotNull(resultDTO.getRefreshToken())
    );
  }

  @Autowired
  UserRepository userRepository;

  @AfterEach
  public void cleanup() {
    userRepository.deleteAll();
  }
}
