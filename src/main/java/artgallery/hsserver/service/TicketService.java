package artgallery.hsserver.service;

import artgallery.hsserver.dto.TicketDTO;
import artgallery.hsserver.exception.*;
import artgallery.hsserver.model.*;
import artgallery.hsserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
  private final TicketRepository ticketRepository;
  private final ExhibitionRepository exhibitionRepository;

  public List<TicketDTO> getAllTickets(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<TicketEntity> ticketPage = ticketRepository.findAll(pageable);

    List<TicketEntity> tickets = ticketPage.getContent();
    return mapToTicketDtoList(tickets);
  }

  public TicketDTO getTicketById(long id) throws TicketDoesNotExistException {
    TicketEntity ticket = ticketRepository.findById(id)
      .orElseThrow(() -> new TicketDoesNotExistException(id));
    return mapToTicketDto(ticket);

  }

  @Transactional
  public TicketDTO createTicket(TicketDTO ticketDTO) throws ExhibitionDoesNotExistException, OrderDoesNotExistException {
    TicketEntity ticket = mapToTicketEntity(ticketDTO, takeExhibition(ticketDTO.getExhibitionId()));
    TicketEntity createdTicket = ticketRepository.save(ticket);
    return mapToTicketDto(createdTicket);
  }

  @Transactional
  public TicketDTO updateTicket(long id, TicketDTO ticketDTO) throws TicketDoesNotExistException, ExhibitionDoesNotExistException {
    Optional<TicketEntity> ticket = ticketRepository.findById(id);
    if (ticket.isPresent()) {
      TicketEntity ticketEntity = ticket.get();
      ticketEntity.setDescription(ticketDTO.getDescription());
      ticketEntity.setPrice(ticketDTO.getPrice());
      ExhibitionEntity exh = exhibitionRepository.findById(ticketDTO.getExhibitionId()).orElseThrow(() ->
        new ExhibitionDoesNotExistException(ticketDTO.getId()));

      ticketEntity.setExhibition(exh);
      TicketEntity newTicket = ticketRepository.save(ticketEntity);
      return mapToTicketDto(newTicket);
    }
    throw new TicketDoesNotExistException(id);
  }

  @Transactional
  public void deleteTicket(long id) {
    if (ticketRepository.existsById(id)) {
      ticketRepository.deleteById(id);
    }
  }

  private TicketDTO mapToTicketDto(TicketEntity ticketEntity) {
    return new TicketDTO(ticketEntity.getId(), ticketEntity.getDescription(),
      ticketEntity.getPrice(), ticketEntity.getExhibition().getId());
  }

  private List<TicketDTO> mapToTicketDtoList(List<TicketEntity> ticketEntities) {
    return ticketEntities.stream().map(this::mapToTicketDto)
      .collect(Collectors.toList());
  }

  private ExhibitionEntity takeExhibition(Long exhibitionId) throws ExhibitionDoesNotExistException {
    ExhibitionEntity exh = exhibitionRepository.findById(exhibitionId).orElseThrow(() ->
      new ExhibitionDoesNotExistException(exhibitionId));
    return exh;
  }

  private TicketEntity mapToTicketEntity(TicketDTO ticketDto, ExhibitionEntity exh){
    TicketEntity ticket = new TicketEntity();
    ticket.setDescription(ticketDto.getDescription());
    ticket.setPrice(ticketDto.getPrice());
    ticket.setExhibition(exh);
    return ticket;
  }
}
