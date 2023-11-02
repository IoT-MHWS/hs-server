package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.*;
import artgallery.hsserver.model.Style;
import artgallery.hsserver.service.RecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recs")
@RequiredArgsConstructor
public class RecController {
  private final RecService recService;

  @PostMapping("/rec-artists")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> getFilteredArtists(@RequestBody RecDTO req) {
    RecValidator validator = new RecValidator();
    validator.validateRec(req);
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(recService.getFilteredArtists(req));
    });
  }

  @GetMapping("/artists/{artistId}/paintings")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('MODERATOR') or hasRole('PUBLIC')")
  public ResponseEntity<?> getPaintingsByArtistId(@PathVariable Long artistId) {
    RecValidator validator = new RecValidator();
    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(recService.getPaintingsByArtistId(artistId));
    });
  }

  private static class RecValidator extends Validator {

    public RecValidator validateRec(RecDTO req) {
      if (req == null ) {
        this.addViolation("req", "req is null");
      }
      if (req.getYearOfBirth() < 0) {
        this.addViolation("req", "year_of_birth is not correct");
      }
      if (Arrays.stream(Style.values()).map(Enum::name).collect(Collectors.toList()).contains(req.getStyle())) {
        this.addViolation("style", "style in not correct");
      }
      return this;
    }
  }
}
