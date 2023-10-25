package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.model.Style;
import artgallery.hsserver.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {
  private final ArtistService artistService;

  @GetMapping("/")
  public ResponseEntity<?> getAllArtists(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size) {
    ArtistValidator validator = new ArtistValidator();
    validator.validateSize(size);
    return ControllerExecutor.execute(validator, () -> {
        Page<ArtistDTO> artistsPage = artistService.getAllArtists(page, size);
        List<ArtistDTO> artists = artistsPage.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(artistsPage.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(artists);
      });
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getArtistById(@PathVariable("id") long id) {
    ArtistValidator validator = new ArtistValidator();
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(artistService.getArtistById(id));
    });
  }


  @PostMapping
  public ResponseEntity<?> createArtist(@RequestBody ArtistDTO req) {
    ArtistValidator validator = new ArtistValidator();
    validator.validateArtist(req);
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.status(HttpStatus.CREATED).body(artistService.createArtist(req));
    });
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> updateArtist(@PathVariable("id") long id, @RequestBody ArtistDTO req)  {
    ArtistValidator validator = new ArtistValidator();
    validator.validateArtist(req);
    return ControllerExecutor.execute(validator, () -> {
      artistService.updateArtist(id, req);
      return ResponseEntity.ok().body("ok");
    });
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteArtist(@PathVariable("id") long id) {
    ArtistValidator validator = new ArtistValidator();
    return ControllerExecutor.execute(validator, () -> {
      artistService.deleteArtist(id);
      return ResponseEntity.ok().body("ok");
    });
  }


  private static class ArtistValidator extends Validator {
    public ArtistValidator validateSize(int size){
      if (size > 50) {
        this.addViolation("size", "size must be <= 50");
      } return this;
    }

    public ArtistValidator validateArtist(ArtistDTO req) {
      if (req == null) {
        this.addViolation("artist", "artist is null");
      }
      if (req.getName() == null) {
        this.addViolation("name", "name is not set or empty");
      }
      if (req.getYearOfBirth() < 0) {
        this.addViolation("year_of_birth", "year_of_birth in not correct");
      }
      if (Arrays.stream(Style.values()).map(Enum::name).collect(Collectors.toList()).contains(req.getStyle())) {
        this.addViolation("style", "style in not correct");
      }
      return this;
    }
  }
}
