package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.exception.GalleryDoesNotExistException;
import artgallery.hsserver.exception.PaintingDoesNotExistException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({TestExtension.class})
@Transactional
public class GalleryControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private GalleryDTO galleryDTO;

  @BeforeAll
  static void createGalleryDTO() {
    galleryDTO = new GalleryDTO();
    galleryDTO.setName("gallery");
    galleryDTO.setAddress("here");
    galleryDTO.setPaintingsId(List.of());
  }

  @Test
  void testGalleryCreation() throws Exception {
    String request = objectMapper.writeValueAsString(galleryDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/galleries")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    GalleryDTO resultDTO = objectMapper.readValue(response.getContentAsString(), GalleryDTO.class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(galleryDTO.getName(), resultDTO.getName()),
      () -> assertEquals(galleryDTO.getAddress(), resultDTO.getAddress()),
      () -> assertEquals(galleryDTO.getPaintingsId(), resultDTO.getPaintingsId())
    );
  }

  @Nested
  class CreatedGalleryTest {
    @Autowired
    GalleryService galleryService;

    @BeforeEach
    public void createGallery() throws PaintingDoesNotExistException {
      galleryDTO = galleryService.createGallery(galleryDTO);
    }

    @Test
    void testGalleryRetrieving() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/galleries/{id}", galleryDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      GalleryDTO resultDTO = objectMapper.readValue(response.getContentAsString(), GalleryDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(galleryDTO.getId(), resultDTO.getId()),
        () -> assertEquals(galleryDTO.getName(), resultDTO.getName()),
        () -> assertEquals(galleryDTO.getAddress(), resultDTO.getAddress())
      );
    }

    @Test
    void testGalleryUpdating() throws Exception {
      String request = objectMapper.writeValueAsString(galleryDTO);

      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/galleries/{id}", galleryDTO.getId())
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
    void testGalleriesListing() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/galleries/")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      GalleryDTO[] results = objectMapper.readValue(response.getContentAsString(), GalleryDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotEquals(0, results.length)
      );
      assertAll(
        () -> assertEquals(galleryDTO.getName(), results[0].getName()),
        () -> assertEquals(galleryDTO.getAddress(), results[0].getAddress())
      );
    }

    @Test
    void testGalleryDeleting() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/galleries/{id}", galleryDTO.getId())
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
    public void deleteGallery() {
      galleryService.deleteGallery(galleryDTO.getId());
    }
  }
}
