package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/galleries")
@RequiredArgsConstructor
public class GalleryController {
  private final GalleryService galleryService;

  @GetMapping("/")
  public ResponseEntity<?> getAllGalleries(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
    GalleryValidator validator = new GalleryValidator();
    validator.validateSize(size);
    return ControllerExecutor.execute(validator, () -> {
        return ResponseEntity.ok().body(galleryService.getAllGalleries(page, size));
      },
      "correct galleries can't be found");
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getGalleryById(@PathVariable("id") long id) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> {
      validator.validateGallery(galleryService.getGalleryById(id));
      return ResponseEntity.ok().body(galleryService.getGalleryById(id));
    }, "this gallery does not exist");
  }

  @PostMapping
  public ResponseEntity<?> createGallery(@RequestBody GalleryDTO req) {
    GalleryValidator validator = new GalleryValidator();
    validator.validateGallery(req);
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(galleryService.createGallery(req));
    }, "cannot create gallery");
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateGallery(@PathVariable("id") long id, @RequestBody GalleryDTO req) {
    GalleryValidator validator = new GalleryValidator();
    validator.validateGallery(req);
    return ControllerExecutor.execute(validator, () -> {
      galleryService.updateGallery(id, req);
      return ResponseEntity.ok().body("ok");
    }, "cannot update gallery");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteGallery(@PathVariable("id") long id) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> {
      galleryService.deleteGallery(id);
      return ResponseEntity.ok().body("ok");
    }, "cannot delete gallery");
  }

  private static class GalleryValidator extends Validator {
    public GalleryValidator validateSize(int size){
      if (size > 50) {
        this.addViolation("size", "size must be <= 50");
      } return this;
    }

    public GalleryValidator validateGallery(GalleryDTO req) {
      if (req == null) {
        this.addViolation("gallery", "gallery is null");
      }
      if (req.getName() == null) {
        this.addViolation("name", "name is not set or empty");
      }
      return this;
    }
  }
}
