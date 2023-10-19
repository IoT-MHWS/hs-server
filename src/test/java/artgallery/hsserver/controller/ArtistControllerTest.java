package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.ArtistDTO;
import artgallery.hsserver.exception.ArtistDoesNotExistException;
import artgallery.hsserver.model.Style;
import artgallery.hsserver.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
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
@ExtendWith({ TestExtension.class })
@Transactional
public class ArtistControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private ArtistDTO artistDTO;

  @BeforeAll
  static void createArtistDTO() {
    artistDTO = new ArtistDTO();
    artistDTO.setName("artist");
    artistDTO.setYearOfBirth(2000);
    artistDTO.setStyle(Style.CUBISM);
  }

  @Test
  void testArtistCreation() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    String request = objectMapper.writeValueAsString(artistDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/artists")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    ArtistDTO resultDTO = objectMapper.readValue(response.getContentAsString(), ArtistDTO.class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(artistDTO.getName(), resultDTO.getName()),
      () -> assertEquals(artistDTO.getStyle(), resultDTO.getStyle()),
      () -> assertEquals(artistDTO.getBio(), resultDTO.getBio()),
      () -> assertEquals(artistDTO.getYearOfBirth(), resultDTO.getYearOfBirth())
    );
  }

  @Test
  void testArtistsCounting() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artists/")
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    String totalCountHeader = response.getHeader("X-Total-Count");
    assertNotNull(totalCountHeader);
    int totalCount = Integer.parseInt(totalCountHeader);
    ArtistDTO[] results = objectMapper.readValue(response.getContentAsString(), ArtistDTO[].class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertTrue(totalCount >= results.length)
    );
  }

  @Nested
  class CreatedArtistTest {
    @Autowired
    ArtistService artistService;

    @BeforeEach
    public void createArtist() {
      artistDTO = artistService.createArtist(artistDTO);
    }

    @Test
    void testArtistRetrieving() throws Exception {
      ObjectMapper objectMapper = new ObjectMapper();

      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artists/{id}", artistDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      ArtistDTO resultDTO = objectMapper.readValue(response.getContentAsString(), ArtistDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(artistDTO.getId(), resultDTO.getId()),
        () -> assertEquals(artistDTO.getName(), resultDTO.getName()),
        () -> assertEquals(artistDTO.getStyle(), resultDTO.getStyle()),
        () -> assertEquals(artistDTO.getBio(), resultDTO.getBio()),
        () -> assertEquals(artistDTO.getYearOfBirth(), resultDTO.getYearOfBirth())
      );
    }

    @Test
    void testArtistsListing() throws Exception {
      ObjectMapper objectMapper = new ObjectMapper();

      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artists/")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      String totalCount = response.getHeader("X-Total-Count");
      ArtistDTO[] results = objectMapper.readValue(response.getContentAsString(), ArtistDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotEquals(0, results.length),
        () -> assertEquals(totalCount, String.valueOf(results.length))
      );
      assertAll(
        () -> assertEquals(artistDTO.getName(), results[0].getName()),
        () -> assertEquals(artistDTO.getName(), results[0].getName()),
        () -> assertEquals(artistDTO.getStyle(), results[0].getStyle()),
        () -> assertEquals(artistDTO.getBio(), results[0].getBio()),
        () -> assertEquals(artistDTO.getYearOfBirth(), results[0].getYearOfBirth())
      );
    }

    @Test
    void testArtistDeleting() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/artists/{id}", artistDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals("ok", response.getContentAsString())
      );
    }

    @AfterEach
    public void deleteArtist() {
      try {
        artistService.deleteArtist(artistDTO.getId());
      } catch (ArtistDoesNotExistException ignore) {
      }
    }
  }
}
