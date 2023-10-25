package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.ArtistDTO;
import artgallery.hsserver.dto.PaintingDTO;
import artgallery.hsserver.dto.RecDTO;
import artgallery.hsserver.exception.ArtistDoesNotExistException;
import artgallery.hsserver.exception.GalleryDoesNotExistException;
import artgallery.hsserver.model.Style;
import artgallery.hsserver.service.ArtistService;
import artgallery.hsserver.service.PaintingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class RecControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private ArtistDTO artistDTO;

  static private PaintingDTO paintingDTO;

  @BeforeAll
  static void setup(@Autowired ArtistService artistService, @Autowired PaintingService paintingService) throws ArtistDoesNotExistException, GalleryDoesNotExistException {
    artistDTO = new ArtistDTO();
    artistDTO.setName("filtered");
    artistDTO.setYearOfBirth(1000);
    artistDTO.setStyle(Style.CUBISM);
    artistDTO = artistService.createArtist(artistDTO);

    paintingDTO = new PaintingDTO();
    paintingDTO.setName("painting");
    paintingDTO.setYearOfCreation(90);
    paintingDTO.setArtistId(artistDTO.getId());
    paintingDTO.setGalleriesId(List.of());
    paintingDTO = paintingService.createPainting(paintingDTO);
  }

  @Test
  void testGettingPaintingsByArtistId() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/recs/artists/{artistId}/paintings", artistDTO.getId())
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    PaintingDTO[] results = objectMapper.readValue(response.getContentAsString(), PaintingDTO[].class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(1, results.length)
    );
    assertAll(
      () -> assertEquals(paintingDTO.getId(), results[0].getId()),
      () -> assertEquals(paintingDTO.getName(), results[0].getName()),
      () -> assertEquals(paintingDTO.getYearOfCreation(), results[0].getYearOfCreation()),
      () -> assertEquals(paintingDTO.getArtistId(), results[0].getArtistId()),
      () -> assertEquals(paintingDTO.getGalleriesId(), results[0].getGalleriesId())
    );
  }

  @Test
  void testGettingFilteredArtists() throws Exception {
    RecDTO recDTO = new RecDTO();
    recDTO.setYearOfBirth(artistDTO.getYearOfBirth());
    recDTO.setStyle(artistDTO.getStyle());
    String request = objectMapper.writeValueAsString(recDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recs/rec-artists")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    ArtistDTO[] results = objectMapper.readValue(response.getContentAsString(), ArtistDTO[].class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(1, results.length)
    );
    assertAll(
      () -> assertEquals(artistDTO.getId(), results[0].getId()),
      () -> assertEquals(artistDTO.getName(), results[0].getName()),
      () -> assertEquals(artistDTO.getBio(), results[0].getBio()),
      () -> assertEquals(artistDTO.getStyle(), results[0].getStyle()),
      () -> assertEquals(artistDTO.getYearOfBirth(), results[0].getYearOfBirth())
    );
  }

  @AfterAll
  static void cleanup(@Autowired ArtistService artistService, @Autowired PaintingService paintingService) {
    paintingService.deletePainting(paintingDTO.getId());
    artistService.deleteArtist(artistDTO.getId());
  }
}
