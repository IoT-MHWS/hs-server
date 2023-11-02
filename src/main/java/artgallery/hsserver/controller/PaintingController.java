package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.service.PaintingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/paintings")
@RequiredArgsConstructor
public class PaintingController {
  private final PaintingService paintingService;

  @GetMapping("/")
  public ResponseEntity<?> getAllPaintings(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
    PaintingValidator validator = new PaintingValidator();
    validator.validateSize(size);
    return ControllerExecutor.execute(validator, () -> ResponseEntity.ok().body(paintingService.getAllPaintings(page, size)));
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getPaintingById(@PathVariable("id") long id) {
    PaintingValidator validator = new PaintingValidator();
    return ControllerExecutor.execute(validator, () -> ResponseEntity.ok().body(paintingService.getPaintingById(id)));
  }


  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR')")
  public ResponseEntity<?> createPainting(@RequestBody PaintingDTO req) {
    PaintingValidator validator = new PaintingValidator();
    validator.validatePainting(req);
    return ControllerExecutor.execute(validator, () -> ResponseEntity.status(HttpStatus.CREATED).body(paintingService.createPainting(req)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR')")
  public ResponseEntity<?> updatePainting(@PathVariable("id") long id, @RequestBody PaintingDTO req)  {
    PaintingValidator validator = new PaintingValidator();
    validator.validatePainting(req);
    return ControllerExecutor.execute(validator, () -> {
      paintingService.updatePainting(id, req);
      return ResponseEntity.ok().body("ok");
    });
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR')")
  public ResponseEntity<?> deletePainting(@PathVariable("id") long id) {
    PaintingValidator validator = new PaintingValidator();
    return ControllerExecutor.execute(validator, () -> {
      paintingService.deletePainting(id);
      return ResponseEntity.noContent().build();
    });
  }

  @GetMapping("/{paintingId}/galleries")
  public ResponseEntity<?> getLinksToGalleries(@PathVariable long paintingId) {
    PaintingValidator validator = new PaintingValidator();
    return ControllerExecutor.execute(validator, () -> ResponseEntity.ok().body(paintingService.getLinksPaintingToGallery(paintingId)));
  }

  private static class PaintingValidator extends Validator {
    public PaintingValidator validateSize(int size){
      if (size > 50) {
        this.addViolation("size", "size must be <= 50");
      } return this;
    }

    public PaintingValidator validatePainting(PaintingDTO req) {
      if (req == null) {
        this.addViolation("painting", "painting is null");
      }
      if (req.getName() == null) {
        this.addViolation("name", "name is not set or empty");
      }
      if (req.getYearOfCreation() < 0) {
        this.addViolation("year_of_creation", "year_of_creation in not correct");
      }
      return this;
    }
  }
}
