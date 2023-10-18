package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.service.TicketService;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class TicketController {
  private final TicketService ticketService;

  @GetMapping("/all")
  public ResponseEntity<?> getAllTickets(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") @Max(50) int size) {
    TicketValidator validator = new TicketValidator();
    List<TicketDTO> reqs = ticketService.getAllTickets(page, size);
    for (TicketDTO ticketDTO : reqs) {
      validator.validateTicket(ticketDTO);
    }
    return ControllerExecutor.execute(validator, () -> {
        return ResponseEntity.ok().body(ticketService.getAllTickets(page, size));
      },
      "correct tickets can't be found");
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getTicketById(@PathVariable("id") long id) {
    TicketValidator validator = new TicketValidator();
    return ControllerExecutor.execute(validator, () -> {
      validator.validateTicket(ticketService.getTicketById(id));
      return ResponseEntity.ok().body(ticketService.getTicketById(id));
    }, "this ticket does not exist");
  }


  @PostMapping
  public ResponseEntity<?> createTicket(@RequestBody TicketDTO req) {
    TicketValidator validator = new TicketValidator();
    validator.validateTicket(req);

    return ControllerExecutor.execute(validator, () -> {
      ticketService.createTicket(req);
      return ResponseEntity.ok().body("ok");
    }, "cannot create ticket");
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> updateTicket(@PathVariable("id") long id, @RequestBody TicketDTO req)  {
    TicketValidator validator = new TicketValidator();
    validator.validateTicket(req);
    return ControllerExecutor.execute(validator, () -> {
      ticketService.updateTicket(id, req);
      return ResponseEntity.ok().body("ok");
    }, "cannot update this ticket");
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTicket(@PathVariable("id") long id) {
    TicketValidator validator = new TicketValidator();

    return ControllerExecutor.execute(validator, () -> {
      ticketService.deleteTicket(id);
      return ResponseEntity.ok().body("ok");
    }, "cannot delete ticket");
  }


  private static class TicketValidator extends Validator {

    public TicketValidator validateTicket(TicketDTO req) {
      if (req == null) {
        this.addViolation("ticket", "ticket is null");
      }
      if (req.getPrice() == null || req.getPrice() < 0) {
        this.addViolation("price", "price is not set or empty");
      }
      if (req.getId() < 0) {
        this.addViolation("id", "id is < 0");
      }
      return this;
    }
  }
}
