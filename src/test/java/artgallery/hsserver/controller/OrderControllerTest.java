package artgallery.hsserver.controller;

import artgallery.hsserver.TestExtension;
import artgallery.hsserver.dto.OrderDTO;
import artgallery.hsserver.exception.OrderDoesNotExistException;
import artgallery.hsserver.exception.TicketDoesNotExistException;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.service.OrderService;
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
@ExtendWith({ TestExtension.class })
@Transactional
public class OrderControllerTest extends AuthorizedControllerTest {
  @Autowired
  private MockMvc mockMvc;

  static private OrderDTO orderDTO;

  @BeforeAll
  static void createOrderDTO() {
    orderDTO = new OrderDTO();
    orderDTO.setDate(new Date());
    orderDTO.setUserLogin("user");
    orderDTO.setTicketsId(List.of());
  }

  @Test
  void testOrderCreation() throws Exception {
    String request = objectMapper.writeValueAsString(orderDTO);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
        .content(request)
        .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn();
    MockHttpServletResponse response = result.getResponse();

    OrderDTO resultDTO = objectMapper.readValue(response.getContentAsString(), OrderDTO.class);

    assertAll(
      () -> assertEquals(200, response.getStatus()),
      () -> assertEquals(orderDTO.getDate(), resultDTO.getDate()),
      () -> assertEquals(orderDTO.getUserLogin(), resultDTO.getUserLogin()),
      () -> assertEquals(orderDTO.getTicketsId(), resultDTO.getTicketsId())
    );
  }

  @Nested
  class CreatedOrderTest {
    @Autowired
    OrderService orderService;

    @BeforeEach
    public void createOrder() throws UserDoesNotExistException, TicketDoesNotExistException {
      orderDTO = orderService.createOrder(orderDTO);
    }

    @Test
    void testOrderRetrieving() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/{id}", orderDTO.getId())
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      OrderDTO resultDTO = objectMapper.readValue(response.getContentAsString(), OrderDTO.class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertEquals(orderDTO.getId(), resultDTO.getId()),
        () -> assertEquals(orderDTO.getDate(), resultDTO.getDate()),
        () -> assertEquals(orderDTO.getUserLogin(), resultDTO.getUserLogin()),
        () -> assertEquals(orderDTO.getTicketsId(), resultDTO.getTicketsId())
      );
    }

    @Test
    void testOrdersListing() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/")
          .header("Authorization", String.format("Bearer %s", tokenDTO.getJwtToken()))
          .accept(MediaType.APPLICATION_JSON))
        .andReturn();
      MockHttpServletResponse response = result.getResponse();

      OrderDTO[] results = objectMapper.readValue(response.getContentAsString(), OrderDTO[].class);

      assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotEquals(0, results.length)
      );
      assertAll(
        () -> assertEquals(orderDTO.getDate(), results[0].getDate()),
        () -> assertEquals(orderDTO.getUserLogin(), results[0].getUserLogin()),
        () -> assertEquals(orderDTO.getTicketsId(), results[0].getTicketsId())
      );
    }

    @Test
    void testOrderDeleting() throws Exception {
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/{id}", orderDTO.getId())
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
    public void deleteOrder() {
      try {
        orderService.deleteOrder(orderDTO.getId());
      } catch (OrderDoesNotExistException ignore) {
      }
    }
  }
}
