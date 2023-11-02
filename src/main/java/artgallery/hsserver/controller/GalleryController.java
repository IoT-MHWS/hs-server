package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.DescriptionDTO;
import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    return ControllerExecutor.execute(validator, () -> ResponseEntity.ok().body(galleryService.getAllGalleries(page, size)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getGalleryById(@PathVariable("id") long id) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> {
      validator.validateGallery(galleryService.getGalleryById(id));
      return ResponseEntity.ok().body(galleryService.getGalleryById(id));
    });
  }

  @PostMapping
  public ResponseEntity<?> createGallery(@RequestBody GalleryDTO req) {
    GalleryValidator validator = new GalleryValidator();
    validator.validateGallery(req);
    return ControllerExecutor.execute(validator, () -> ResponseEntity.status(HttpStatus.CREATED).body(galleryService.createGallery(req)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateGallery(@PathVariable("id") long id, @RequestBody GalleryDTO req) {
    GalleryValidator validator = new GalleryValidator();
    validator.validateGallery(req);
    return ControllerExecutor.execute(validator, () -> ResponseEntity.status(HttpStatus.OK).body(galleryService.updateGallery(id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteGallery(@PathVariable("id") long id) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> {
      galleryService.deleteGallery(id);
      return ResponseEntity.noContent().build();
    });
  }

  @GetMapping("/{galleryId}/paintings")
  public ResponseEntity<?> getLinksToPaintings(@PathVariable long galleryId) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> ResponseEntity.ok().body(galleryService.getLinksGalleryToPainting(galleryId)));
  }

  @PutMapping("/{galleryId}/paintings/{paintingId}")
  public ResponseEntity<?> createOrUpdateLink(@PathVariable long galleryId, @PathVariable long paintingId,
                                              @RequestBody DescriptionDTO linkDto) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> {
      boolean isNewLink = (galleryService.existsByGalleryIdAndPaintingId(galleryId, paintingId));
      return ResponseEntity.status(isNewLink ? HttpStatus.CREATED : HttpStatus.OK)
        .body(galleryService.createOrUpdateLinkGalleryToPainting(galleryId, paintingId, linkDto, isNewLink));
    });
  }

  @DeleteMapping("/{galleryId}/paintings/{paintingId}")
  public ResponseEntity<?> deleteLink(@PathVariable long galleryId, @PathVariable long paintingId) {
    GalleryValidator validator = new GalleryValidator();
    return ControllerExecutor.execute(validator, () -> {
      galleryService.deleteLink(galleryId, paintingId);
      return ResponseEntity.noContent().build();
    });
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
