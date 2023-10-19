package artgallery.hsserver.service;

import artgallery.hsserver.dto.OrderDTO;
import artgallery.hsserver.exception.OrderDoesNotExistException;
import artgallery.hsserver.exception.TicketDoesNotExistException;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.model.OrderEntity;
import artgallery.hsserver.model.TicketEntity;
import artgallery.hsserver.model.UserEntity;
import artgallery.hsserver.repository.OrderRepository;
import artgallery.hsserver.repository.TicketRepository;
import artgallery.hsserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final TicketRepository ticketRepository;

  public List<OrderDTO> getAllOrders(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<OrderEntity> orderPage = orderRepository.findAll(pageable);

    List<OrderEntity> orders = orderPage.getContent();
    return mapToOrderDtoList(orders);
  }

  public OrderDTO getOrderById(long id) throws OrderDoesNotExistException {
    OrderEntity order = orderRepository.findById(id)
      .orElseThrow(() -> new OrderDoesNotExistException(id));
    return mapToOrderDto(order);

  }

  @Transactional
  public OrderDTO createOrder(OrderDTO orderDTO) throws UserDoesNotExistException, TicketDoesNotExistException {
    OrderEntity order = mapToOrderEntity(orderDTO);
    List<TicketEntity> ticketEntities = new ArrayList<>();
    for (Long ticketId : orderDTO.getTicketsId()) {
      Optional<TicketEntity> ticketOptional = ticketRepository.findById(ticketId);
      if (ticketOptional.isPresent()) {
        ticketEntities.add(ticketOptional.get());
      } else {
        throw new TicketDoesNotExistException(ticketId);
      }
    }
    order.setTicketEntities(ticketEntities);
    OrderEntity createdOrder = orderRepository.save(order);
    return mapToOrderDto(createdOrder);
  }

  @Transactional
  public OrderDTO updateOrder(long id, OrderDTO orderDTO) throws OrderDoesNotExistException, UserDoesNotExistException {
    Optional<OrderEntity> order = orderRepository.findById(id);
    if (order.isPresent()) {
      OrderEntity ord = order.get();
      ord.setDate(orderDTO.getDate());
      UserEntity ott = userRepository.findByLogin(orderDTO.getUserLogin()).orElseThrow(() ->
        new UserDoesNotExistException(orderDTO.getUserLogin()));
      ord.setUser(ott);
      OrderEntity newOrder = orderRepository.save(ord);
      return mapToOrderDto(newOrder);
    }
    throw new OrderDoesNotExistException(id);
  }

  @Transactional
  public void deleteOrder(long id) throws OrderDoesNotExistException {
    if (orderRepository.existsById(id)) {
      orderRepository.deleteById(id);
    } else {
      throw new OrderDoesNotExistException(id);
    }
  }

  private OrderDTO mapToOrderDto(OrderEntity order) {
    List<Long> ticketIds = order.getTicketEntities().stream()
      .map(TicketEntity::getId)
      .collect(Collectors.toList());
    return new OrderDTO(order.getId(), order.getDate(), order.getUser().getLogin(), ticketIds);
  }

  private List<OrderDTO> mapToOrderDtoList(List<OrderEntity> orders) {
    return orders.stream().map(this::mapToOrderDto)
      .collect(Collectors.toList());
  }
  private OrderEntity mapToOrderEntity(OrderDTO orderDto) throws UserDoesNotExistException {
    OrderEntity order = new OrderEntity();
    order.setDate(orderDto.getDate());
    UserEntity userEntity = userRepository.findByLogin(orderDto.getUserLogin()).orElseThrow(() ->
      new UserDoesNotExistException(orderDto.getUserLogin()));
    List<Long> ticketIds = orderDto.getTicketsId();
    List<TicketEntity> ticketEntities = ticketRepository.findAllById(ticketIds);

    order.setUser(userEntity);
    order.setTicketEntities(ticketEntities);
    return order;
  }
}
