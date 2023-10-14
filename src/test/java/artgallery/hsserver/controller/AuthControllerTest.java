package artgallery.hsserver.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.util.StringModifier;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({ TestExtension.class })
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testRegisterUser() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    UserDTO userDTO = new UserDTO();
    userDTO.setLogin(StringModifier.getUniqueString("user"));
    userDTO.setPassword("user");
    String userDTOString = objectMapper.writeValueAsString(userDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
        .content(userDTOString)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andReturn();

    System.out.println(result.getResponse().getContentAsString());
  }

}
