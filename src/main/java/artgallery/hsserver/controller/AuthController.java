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
@RequestMapping(path = "/api/v1/auth", produces = {"application/json", "application/xml"})
@RequiredArgsConstructor
class AuthController {

  private final AuthService authService;

  @PostMapping(path = "/register")
  public ResponseEntity<?> register(@RequestBody UserDTO req) {
    UserValidator validator = new UserValidator();
    validator.validateLogin(req).validatePassword(req);

    try {
      if (validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
          new Exception(validator.getDescription()));
      }

      try {
        authService.register(req);
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "register can't be done", ex);
      }

      return ResponseEntity.ok().body(new MessageDTO("ok"));

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  @PostMapping(path = "/login")
  public ResponseEntity<?> login(@RequestBody UserDTO req) {
    UserValidator validator = new UserValidator();
    validator.validateLogin(req).validatePassword(req);

    try {
      if (validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
          new Exception(validator.getDescription()));
      }

      try {
        var tokenDTO = authService.login(req);
        return ResponseEntity.ok().body(tokenDTO);

      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "login can't be done", ex);
      }

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestHeader HttpHeaders reqHeaders) {
    try {
      try {
        var tokenDTO = authService.refreshToken(reqHeaders);
        return ResponseEntity.ok().body(tokenDTO);
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "refresh can't be done", ex);
      }
    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  private class UserValidator extends Validator {
    public UserValidator validateLogin(UserDTO req) {
      if (req.getLogin() == null || req.getLogin().isEmpty()) {
        this.addViolation("login", "user login in not set or empty");
      }
      return this;
    }

    public UserValidator validatePassword(UserDTO req) {
      if (req.getPassword() == null || req.getPassword().isEmpty()) {
        this.addViolation("password", "user password in not set or empty");
      }
      return this;
    }
  }
}
