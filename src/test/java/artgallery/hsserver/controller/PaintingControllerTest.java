package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.ArtistDTO;
import artgallery.hsserver.dto.PaintingDTO;
import artgallery.hsserver.exception.ArtistDoesNotExistException;
import artgallery.hsserver.exception.GalleryDoesNotExistException;
import artgallery.hsserver.exception.PaintingDoesNotExistException;
import artgallery.hsserver.model.Style;
import artgallery.hsserver.service.ArtistService;
import artgallery.hsserver.service.PaintingService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class PaintingControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private PaintingDTO paintingDTO;

  @BeforeAll
  static void createPaintingDTO(@Autowired ArtistService artistService) {
    ArtistDTO artistDTO = new ArtistDTO();
    artistDTO.setName("artist");
    artistDTO.setYearOfBirth(2000);
    artistDTO.setStyle(Style.CUBISM);
    artistDTO = artistService.createArtist(artistDTO);

    paintingDTO = new PaintingDTO();
    paintingDTO.setName("painting");
    paintingDTO.setYearOfCreation(90);
    paintingDTO.setArtistId(artistDTO.getId());
    paintingDTO.setGalleriesId(List.of());
  }

  @Test
  void testPaintingCreation() throws Exception {
    String request = objectMapper.writeValueAsString(paintingDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/paintings")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    PaintingDTO resultDTO = objectMapper.readValue(response.getContentAsString(), PaintingDTO.class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(paintingDTO.getName(), resultDTO.getName()),
      () -> assertEquals(paintingDTO.getYearOfCreation(), resultDTO.getYearOfCreation()),
      () -> assertEquals(paintingDTO.getArtistId(), resultDTO.getArtistId()),
      () -> assertEquals(paintingDTO.getGalleriesId(), resultDTO.getGalleriesId())
    );
  }

  @Nested
  class CreatedPaintingTest {
    @Autowired
    PaintingService paintingService;

    @BeforeEach
    public void createPainting() throws ArtistDoesNotExistException, GalleryDoesNotExistException {
      paintingDTO = paintingService.createPainting(paintingDTO);
    }

    @Test
    void testPaintingRetrieving() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/paintings/{id}", paintingDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      PaintingDTO resultDTO = objectMapper.readValue(response.getContentAsString(), PaintingDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(paintingDTO.getId(), resultDTO.getId()),
        () -> assertEquals(paintingDTO.getName(), resultDTO.getName()),
        () -> assertEquals(paintingDTO.getYearOfCreation(), resultDTO.getYearOfCreation()),
        () -> assertEquals(paintingDTO.getArtistId(), resultDTO.getArtistId()),
        () -> assertEquals(paintingDTO.getGalleriesId(), resultDTO.getGalleriesId())
      );
    }

    @Test
    void testPaintingsListing() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/paintings/")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      PaintingDTO[] results = objectMapper.readValue(response.getContentAsString(), PaintingDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotEquals(0, results.length)
      );
      assertAll(
        () -> assertEquals(paintingDTO.getName(), results[0].getName()),
        () -> assertEquals(paintingDTO.getYearOfCreation(), results[0].getYearOfCreation()),
        () -> assertEquals(paintingDTO.getArtistId(), results[0].getArtistId()),
        () -> assertEquals(paintingDTO.getGalleriesId(), results[0].getGalleriesId())
      );
    }

    @Test
    void testPaintingDeleting() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/paintings/{id}", paintingDTO.getId())
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
    public void deletePainting() {
      try {
        paintingService.deletePainting(paintingDTO.getId());
      } catch (GalleryDoesNotExistException | PaintingDoesNotExistException ignore) {
      }
    }
  }
}
