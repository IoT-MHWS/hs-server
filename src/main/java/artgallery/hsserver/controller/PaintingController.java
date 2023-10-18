package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.service.PaintingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/painting")
@RequiredArgsConstructor
public class PaintingController {
  private final PaintingService paintingService;

  @GetMapping("/all")
  public ResponseEntity<?> getAllOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {
    PaintingValidator validator = new PaintingValidator();
    List<PaintingDTO> reqs = paintingService.getAllPaintings(page, size);
    for (PaintingDTO paintingDTO : reqs) {
      validator.validatePainting(paintingDTO);
    }
    return ControllerExecutor.execute(validator, () -> {
        return ResponseEntity.ok().body(paintingService.getAllPaintings(page, size));
      },
      "correct paintings can't be found");
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getPaintingById(@PathVariable("id") long id) {
    PaintingValidator validator = new PaintingValidator();
    return ControllerExecutor.execute(validator, () -> {
      validator.validatePainting(paintingService.getPaintingById(id));
      return ResponseEntity.ok().body(paintingService.getPaintingById(id));
    }, "this painting does not exist");
  }


  @PostMapping
  public ResponseEntity<?> createPainting(@RequestBody PaintingDTO req) {
    PaintingValidator validator = new PaintingValidator();
    validator.validatePainting(req);

    return ControllerExecutor.execute(validator, () -> {
      paintingService.createPainting(req);
      return ResponseEntity.ok().body("ok");
    }, "cannot create painting");
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> updatePainting(@PathVariable("id") long id, @RequestBody PaintingDTO req)  {
    PaintingValidator validator = new PaintingValidator();
    validator.validatePainting(req);
    return ControllerExecutor.execute(validator, () -> {
      paintingService.updatePainting(id, req);
      return ResponseEntity.ok().body("ok");
    }, "cannot update this painting");
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePainting(@PathVariable("id") long id) {
    PaintingValidator validator = new PaintingValidator();

    return ControllerExecutor.execute(validator, () -> {
      paintingService.deletePainting(id);
      return ResponseEntity.ok().body("ok");
    }, "cannot delete painting");
  }


  private static class PaintingValidator extends Validator {

    public PaintingValidator validatePainting(PaintingDTO req) {
      if (req == null) {
        this.addViolation("painting", "painting is null");
      }
      if (req.getName() == null) {
        this.addViolation("name", "name is not set or empty");
      }
      if (req.getId() < 0) {
        this.addViolation("id", "id is < 0");
      }
      return this;
    }
  }
}
