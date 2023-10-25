package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.ExhibitionDTO;
import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.exception.GalleryDoesNotExistException;
import artgallery.hsserver.exception.PaintingDoesNotExistException;
import artgallery.hsserver.service.ExhibitionService;
import artgallery.hsserver.service.GalleryService;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class ExhibitionControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private ExhibitionDTO exhibitionDTO;

  @BeforeAll
  static void createExhibitionDTO(@Autowired GalleryService galleryService) throws PaintingDoesNotExistException {
    GalleryDTO galleryDTO = new GalleryDTO();
    galleryDTO.setName("gallery");
    galleryDTO.setAddress("here");
    galleryDTO.setPaintingsId(List.of());
    galleryDTO = galleryService.createGallery(galleryDTO);

    exhibitionDTO = new ExhibitionDTO();
    exhibitionDTO.setName("exhibition");
    exhibitionDTO.setStartDate(new Date());
    exhibitionDTO.setEndDate(new Date());
    exhibitionDTO.setGalleryId(galleryDTO.getId());
  }

  @Test
  void testExhibitionCreation() throws Exception {
    String request = objectMapper.writeValueAsString(exhibitionDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/exhibitions")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    ExhibitionDTO resultDTO = objectMapper.readValue(response.getContentAsString(), ExhibitionDTO.class);

    assertAll(
      () -> assertEquals(201, response.getStatus()),
      () -> assertEquals(exhibitionDTO.getName(), resultDTO.getName()),
      () -> assertEquals(exhibitionDTO.getStartDate(), resultDTO.getStartDate()),
      () -> assertEquals(exhibitionDTO.getEndDate(), resultDTO.getEndDate()),
      () -> assertEquals(exhibitionDTO.getGalleryId(), resultDTO.getGalleryId())
    );
  }

  @Nested
  class CreatedExhibitionTest {
    @Autowired
    ExhibitionService exhibitionService;

    @BeforeEach
    public void createExhibition() throws GalleryDoesNotExistException {
      exhibitionDTO = exhibitionService.createExhibition(exhibitionDTO);
    }

    @Test
    void testExhibitionRetrieving() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/exhibitions/{id}", exhibitionDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      ExhibitionDTO resultDTO = objectMapper.readValue(response.getContentAsString(), ExhibitionDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(exhibitionDTO.getId(), resultDTO.getId()),
        () -> assertEquals(exhibitionDTO.getName(), resultDTO.getName()),
        () -> assertEquals(exhibitionDTO.getStartDate(), resultDTO.getStartDate()),
        () -> assertEquals(exhibitionDTO.getEndDate(), resultDTO.getEndDate()),
        () -> assertEquals(exhibitionDTO.getGalleryId(), resultDTO.getGalleryId())
      );
    }

    @Test
    void testExhibitionUpdating() throws Exception {
      String request = objectMapper.writeValueAsString(exhibitionDTO);

      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/exhibitions/{id}", exhibitionDTO.getId())
          .content(request)
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals("ok", response.getContentAsString())
      );
    }

    @Test
    void testExhibitionsListing() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/exhibitions/")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      ExhibitionDTO[] results = objectMapper.readValue(response.getContentAsString(), ExhibitionDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotEquals(0, results.length)
      );
      assertAll(
        () -> assertEquals(exhibitionDTO.getName(), results[0].getName()),
        () -> assertEquals(exhibitionDTO.getStartDate(), results[0].getStartDate()),
        () -> assertEquals(exhibitionDTO.getEndDate(), results[0].getEndDate()),
        () -> assertEquals(exhibitionDTO.getGalleryId(), results[0].getGalleryId())
      );
    }

    @Test
    void testExhibitionDeleting() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/exhibitions/{id}", exhibitionDTO.getId())
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
    public void deleteExhibition() {
      exhibitionService.deleteExhibition(exhibitionDTO.getId());
    }
  }
}
