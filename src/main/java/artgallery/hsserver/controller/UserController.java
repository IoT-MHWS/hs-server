package artgallery.hsserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.MessageDTO;
import artgallery.hsserver.dto.UserDTO;

@RestController
@RequestMapping("/api/v1/user")
class UserController {

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserDTO req) {
    UserValidator validator = new UserValidator();
    validator = validator.validateLogin(req).validatePassword(req);

    if (validator.hasViolations()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validator.getViolations());
    }

    return ResponseEntity.ok().body(new MessageDTO("ok"));
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
