package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.MessageDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = { "application/json", "application/xml" })
@RequiredArgsConstructor
class AuthController {

  private final AuthService authService;

  public interface RunController {
    public ResponseEntity<?> run() throws Exception;
  }

  public ResponseEntity<?> controller(Validator validator, RunController controllerFunc, String serviceErrMsg) {
    try {
      if (validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
            new Exception(validator.getDescription()));
      }

      try {
        return controllerFunc.run();
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "register can't be done", ex);
      }

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  @PostMapping(path = "/register")
  public ResponseEntity<?> register(@RequestBody UserDTO req) {
    AuthValidator validator = new AuthValidator();
    validator.validateLogin(req).validatePassword(req);

    return controller(validator, () -> {
      authService.register(req);
      return ResponseEntity.ok().body("ok");
    },
        "register can't be done");
  }

  @PostMapping(path = "/login")
  public ResponseEntity<?> login(@RequestBody UserDTO req) {
    AuthValidator validator = new AuthValidator();
    validator.validateLogin(req).validatePassword(req);

    return controller(validator, () -> {
      var tokenDTO = authService.login(req);
      return ResponseEntity.ok().body(tokenDTO);
    },
        "login can't be done");

  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestHeader HttpHeaders reqHeaders) {
    return controller(new Validator(), () -> {
      var tokenDTO = authService.refreshToken(reqHeaders);
      return ResponseEntity.ok().body(tokenDTO);
    },
        "refresh can't be done");
  }

  private class AuthValidator extends Validator {
    public AuthValidator validateLogin(UserDTO req) {
      if (req.getLogin() == null || req.getLogin().isEmpty()) {
        this.addViolation("login", "user login in not set or empty");
      }
      return this;
    }

    public AuthValidator validatePassword(UserDTO req) {
      if (req.getPassword() == null || req.getPassword().isEmpty()) {
        this.addViolation("password", "user password in not set or empty");
      }
      return this;
    }
  }
}
