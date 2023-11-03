package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.dto.PaintingDTO;
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
      () -> assertEquals(201, response.getStatus()),
      () -> assertEquals(galleryDTO.getName(), resultDTO.getName()),
      () -> assertEquals(galleryDTO.getAddress(), resultDTO.getAddress())
    );
  }

  @Test
  void testGalleryCreationBadData() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/galleries")
        .content("{}")
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(422, response.getStatus());
  }

  @Nested
  class CreatedGalleryTest {
    @Autowired
    GalleryService galleryService;

    @BeforeEach
    public void createGallery() {
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
    void testGalleryNotFound() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/galleries/2000")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();
      assertEquals(404, response.getStatus());
    }

    @Test
    void testGalleryPaintingsListing() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/galleries/{id}/paintings", galleryDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      PaintingDTO[] resultDTO = objectMapper.readValue(response.getContentAsString(), PaintingDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(0, resultDTO.length)
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

      GalleryDTO resultDTO = objectMapper.readValue(response.getContentAsString(), GalleryDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(galleryDTO.getId(), resultDTO.getId()),
        () -> assertEquals(galleryDTO.getName(), resultDTO.getName()),
        () -> assertEquals(galleryDTO.getAddress(), resultDTO.getAddress())
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
        () -> assertEquals(204, response.getStatus()),
        () -> assertEquals(0, response.getContentLength())
      );
    }

    @AfterEach
    public void deleteGallery() {
      galleryService.deleteGallery(galleryDTO.getId());
    }
  }
}
