package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.service.ExhibitionService;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exhibition")
@RequiredArgsConstructor
public class ExhibitionController {
  private final ExhibitionService exhibitionService;

  @GetMapping("/all")
  public ResponseEntity<?> getAllExhibitions(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") @Max(50) int size) {
    ExhibitionValidator validator = new ExhibitionValidator();
    List<ExhibitionDTO> reqs = exhibitionService.getAllExhibitions(page, size);
    for (ExhibitionDTO exhibitionDTO : reqs) {
      validator.validateExhibition(exhibitionDTO);
    }
    return ControllerExecutor.execute(validator, () -> {
        return ResponseEntity.ok().body(exhibitionService.getAllExhibitions(page, size));
      },
      "correct exhibitions can't be found");
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getExhibitionById(@PathVariable("id") long id) {
    ExhibitionValidator validator = new ExhibitionValidator();
    return ControllerExecutor.execute(validator, () -> {
      validator.validateExhibition(exhibitionService.getExhibitionById(id));
      return ResponseEntity.ok().body(exhibitionService.getExhibitionById(id));
    }, "this exhibition does not exist");
  }


  @PostMapping
  public ResponseEntity<?> createExhibition(@RequestBody ExhibitionDTO req) {
    ExhibitionValidator validator = new ExhibitionValidator();
    validator.validateExhibition(req);

    return ControllerExecutor.execute(validator, () -> {
      exhibitionService.createExhibition(req);
      return ResponseEntity.ok().body("ok");
    }, "cannot create exhibition");
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> updateExhibition(@PathVariable("id") long id, @RequestBody ExhibitionDTO req)  {
    ExhibitionValidator validator = new ExhibitionValidator();
    validator.validateExhibition(req);
    return ControllerExecutor.execute(validator, () -> {
      exhibitionService.updateExhibition(id, req);
      return ResponseEntity.ok().body("ok");
    }, "cannot update this exhibition");
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteExhibition(@PathVariable("id") long id) {
    ExhibitionValidator validator = new ExhibitionValidator();

    return ControllerExecutor.execute(validator, () -> {
      exhibitionService.deleteExhibition(id);
      return ResponseEntity.ok().body("ok");
    }, "cannot delete exhibition");
  }


  private static class ExhibitionValidator extends Validator {

    public ExhibitionValidator validateExhibition(ExhibitionDTO req) {
      if (req == null) {
        this.addViolation("exhibition", "exhibition is null");
      }
      if (req.getName() == null || req.getStartDate() == null|| req.getEndDate()== null) {
        this.addViolation("exhibition", "exhibition is not set or empty");
      }
      if (req.getId() < 0) {
        this.addViolation("id", "id is < 0");
      }
      return this;
    }
  }
}
