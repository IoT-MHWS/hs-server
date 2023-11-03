package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.MessageDTO;
import artgallery.hsserver.dto.RoleDTO;
import artgallery.hsserver.dto.UserCreatedDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.exception.UserAlreadyExists;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.exception.UserRoleAlreadyExists;
import artgallery.hsserver.model.Role;
import artgallery.hsserver.service.UserService;
import jakarta.transaction.Transactional;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class UserControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private UserDTO userDTO;

  @BeforeAll
  static void setup(@Autowired UserService userService) throws UserAlreadyExists, RoleDoesNotExistException, UserDoesNotExistException, UserRoleAlreadyExists {
    userService.addRole(username, Role.ADMIN);

    userDTO = new UserDTO();
    userDTO.setLogin("username-2");
    userDTO.setPassword("password-2");
    userService.register(userDTO);
  }

  @Autowired
  UserService userService;

  @Test
  public void testRegisterUser() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setLogin("username-3");
    userDTO.setPassword("password-3");

    String request = objectMapper.writeValueAsString(userDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/create")
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .content(request)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    UserCreatedDTO resultDTO = objectMapper.readValue(response.getContentAsString(), UserCreatedDTO.class);

    assertAll(
      () -> assertEquals(201, response.getStatus()),
      () -> assertEquals(userDTO.getLogin(), resultDTO.getLogin())
    );
  }

  @Test
  public void testRegisterUserConflict() throws Exception {
    String request = objectMapper.writeValueAsString(userDTO);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/create")
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .content(request)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(409, response.getStatus());
  }

  @Test
  void testRoleAdding() throws Exception {
    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setRole(Role.MODERATOR);
    String request = objectMapper.writeValueAsString(roleDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/{login}/roles/add", userDTO.getLogin())
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    MessageDTO messageDTO = objectMapper.readValue(response.getContentAsString(), MessageDTO.class);

    assertAll(
      () -> assertEquals(201, response.getStatus()),
      () -> assertEquals("ok", messageDTO.getMsg())
    );

    userService.removeRole(userDTO.getLogin(), Role.MODERATOR);
  }

  @Test
  void testRoleRemoving() throws Exception {
    userService.addRole(userDTO.getLogin(), Role.MODERATOR);

    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setRole(Role.MODERATOR);
    String request = objectMapper.writeValueAsString(roleDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/{login}/roles/remove", userDTO.getLogin())
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    assertAll(
      () -> assertEquals(204, response.getStatus()),
      () -> assertEquals(0, response.getContentLength())
    );
  }

  @Test
  void testRoleGetting() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{login}/roles", userDTO.getLogin())
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    RoleDTO[] results = objectMapper.readValue(response.getContentAsString(), RoleDTO[].class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(1, results.length),
      () -> assertEquals(Role.PUBLIC, results[0].getRole())
    );
  }
}
