package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {
  private final ExhibitionService exhibitionService;

  @GetMapping("/")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> getAllExhibitions(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {
    ExhibitionValidator validator = new ExhibitionValidator();
    validator.validateSize(size);
    return ControllerExecutor.execute(validator, () -> {
        return ResponseEntity.ok().body(exhibitionService.getAllExhibitions(page, size));
      });
  }


  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> getExhibitionById(@PathVariable("id") long id) {
    ExhibitionValidator validator = new ExhibitionValidator();
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(exhibitionService.getExhibitionById(id));
    });
  }


  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> createExhibition(@RequestBody ExhibitionDTO req) {
    ExhibitionValidator validator = new ExhibitionValidator();
    validator.validateExhibition(req);
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.status(HttpStatus.CREATED).body(exhibitionService.createExhibition(req));
    });
  }


  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> updateExhibition(@PathVariable("id") long id, @RequestBody ExhibitionDTO req)  {
    ExhibitionValidator validator = new ExhibitionValidator();
    validator.validateExhibition(req);
    return ControllerExecutor.execute(validator, () -> {
      exhibitionService.updateExhibition(id, req);
      return ResponseEntity.ok().body("ok");
    });
  }


  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> deleteExhibition(@PathVariable("id") long id) {
    ExhibitionValidator validator = new ExhibitionValidator();
    return ControllerExecutor.execute(validator, () -> {
      exhibitionService.deleteExhibition(id);
      return ResponseEntity.noContent().build();
    });
  }


  private static class ExhibitionValidator extends Validator {
    public ExhibitionValidator validateSize(int size){
      if (size > 50) {
        this.addViolation("size", "size must be <= 50");
      } return this;
    }

    public ExhibitionValidator validateExhibition(ExhibitionDTO req) {
      if (req == null) {
        this.addViolation("exhibition", "exhibition is null");
      }
      if (req.getName() == null || req.getStartDate() == null|| req.getEndDate()== null) {
        this.addViolation("exhibition", "exhibition is not set or empty");
      }
      return this;
    }
  }
}
