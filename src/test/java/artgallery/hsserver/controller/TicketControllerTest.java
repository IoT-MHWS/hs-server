package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.ExhibitionDTO;
import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.dto.TicketDTO;
import artgallery.hsserver.exception.*;
import artgallery.hsserver.service.ExhibitionService;
import artgallery.hsserver.service.GalleryService;
import artgallery.hsserver.service.TicketService;
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
public class TicketControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private GalleryDTO galleryDTO;
  static private ExhibitionDTO exhibitionDTO;
  static private TicketDTO ticketDTO;

  @BeforeAll
  static void createTicketDTO(@Autowired GalleryService galleryService, @Autowired ExhibitionService exhibitionService) throws PaintingDoesNotExistException, GalleryDoesNotExistException {
    galleryDTO = new GalleryDTO();
    galleryDTO.setName("gallery");
    galleryDTO.setAddress("here");
    galleryDTO.setPaintingsId(List.of());
    galleryDTO = galleryService.createGallery(galleryDTO);

    exhibitionDTO = new ExhibitionDTO();
    exhibitionDTO.setName("exhibition");
    exhibitionDTO.setStartDate(new Date());
    exhibitionDTO.setEndDate(new Date());
    exhibitionDTO.setGalleryId(galleryDTO.getId());
    exhibitionDTO = exhibitionService.createExhibition(exhibitionDTO);

    ticketDTO = new TicketDTO();
    ticketDTO.setDescription("ticket");
    ticketDTO.setPrice(1000000);
    ticketDTO.setExhibitionId(exhibitionDTO.getId());
  }

  @Test
  void testTicketCreation() throws Exception {
    String request = objectMapper.writeValueAsString(ticketDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tickets")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    TicketDTO resultDTO = objectMapper.readValue(response.getContentAsString(), TicketDTO.class);

    assertAll(
      () -> assertEquals(201, response.getStatus()),
      () -> assertEquals(ticketDTO.getDescription(), resultDTO.getDescription()),
      () -> assertEquals(ticketDTO.getPrice(), resultDTO.getPrice()),
      () -> assertEquals(ticketDTO.getExhibitionId(), resultDTO.getExhibitionId())
    );
  }

  @Nested
  class CreatedTicketTest {
    @Autowired
    TicketService ticketService;

    @BeforeEach
    public void createTicket() throws OrderDoesNotExistException, ExhibitionDoesNotExistException {
      ticketDTO = ticketService.createTicket(ticketDTO);
    }

    @Test
    void testTicketRetrieving() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tickets/{id}", ticketDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      TicketDTO resultDTO = objectMapper.readValue(response.getContentAsString(), TicketDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(ticketDTO.getId(), resultDTO.getId()),
        () -> assertEquals(ticketDTO.getDescription(), resultDTO.getDescription()),
        () -> assertEquals(ticketDTO.getPrice(), resultDTO.getPrice()),
        () -> assertEquals(ticketDTO.getExhibitionId(), resultDTO.getExhibitionId())
      );
    }

    @Test
    void testTicketUpdating() throws Exception {
      String request = objectMapper.writeValueAsString(ticketDTO);

      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tickets/{id}", ticketDTO.getId())
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
    void testTicketsListing() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tickets/")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      TicketDTO[] results = objectMapper.readValue(response.getContentAsString(), TicketDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotEquals(0, results.length)
      );
      assertAll(
        () -> assertEquals(ticketDTO.getDescription(), results[0].getDescription()),
        () -> assertEquals(ticketDTO.getPrice(), results[0].getPrice()),
        () -> assertEquals(ticketDTO.getExhibitionId(), results[0].getExhibitionId())
      );
    }

    @Test
    void testTicketDeleting() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tickets/{id}", ticketDTO.getId())
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
    public void deleteTicket() {
      ticketService.deleteTicket(ticketDTO.getId());
    }
  }

  @AfterAll
  static void cleanup(@Autowired GalleryService galleryService, @Autowired ExhibitionService exhibitionService) {
    exhibitionService.deleteExhibition(exhibitionDTO.getId());
    galleryService.deleteGallery(galleryDTO.getId());
  }
}
