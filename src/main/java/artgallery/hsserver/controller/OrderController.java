package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @GetMapping("/")
  public ResponseEntity<?> getAllOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
    OrderValidator validator = new OrderValidator();
    validator.validateSize(size);
    return ControllerExecutor.execute(validator, () -> {
        return ResponseEntity.ok().body(orderService.getAllOrders(page, size));
      },
      "correct orders can't be found");
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getOrderById(@PathVariable("id") long id) {
    OrderValidator validator = new OrderValidator();
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(orderService.getOrderById(id));
    }, "this order does not exist");
  }


  @PostMapping
  public ResponseEntity<?> createOrder(@RequestBody OrderDTO req) {
    OrderValidator validator = new OrderValidator();
    validator.validateOrder(req);
    return ControllerExecutor.execute(validator, () -> {
      orderService.createOrder(req);
      return ResponseEntity.ok().body("ok");
    }, "cannot create order");
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> updateOrder(@PathVariable("id") long id, @RequestBody OrderDTO req)  {
    OrderValidator validator = new OrderValidator();;
    validator.validateOrder(req);
    return ControllerExecutor.execute(validator, () -> {
      orderService.updateOrder(id, req);
      return ResponseEntity.ok().body("ok");
    }, "cannot update this order");
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteOrder(@PathVariable("id") long id) {
    OrderValidator validator = new OrderValidator();
    return ControllerExecutor.execute(validator, () -> {
      orderService.deleteOrder(id);
      return ResponseEntity.ok().body("ok");
    }, "cannot delete order");
  }


  private static class OrderValidator extends Validator {
    public OrderValidator validateSize(int size){
      if (size > 50) {
        this.addViolation("size", "size must be <= 50");
      } return this;
    }

    public OrderValidator validateOrder(OrderDTO req) {
      if (req == null) {
        this.addViolation("order", "order is null");
      }
      if (req.getDate() == null) {
        this.addViolation("date", "date is not set or empty");
      }
      return this;
    }
  }
}
