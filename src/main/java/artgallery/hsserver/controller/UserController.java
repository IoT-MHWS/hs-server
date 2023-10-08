package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.MessageDTO;
import artgallery.hsserver.dto.RoleDTO;
import artgallery.hsserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 */
@RestController
@RequestMapping(path = "/api/v1/user", produces = {"application/json", "application/xml"})
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(path = "/{login}/role/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> add(@PathVariable("login") String name, RoleDTO roleDTO) {
    var validator = new UserValidator().validateLogin(name).validateRole(roleDTO);

    try {
      if (validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
          new Exception(validator.getDescription()));
      }

      try {
        userService.addRole(name, roleDTO.getRole());
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "add failed", ex);
      }

      return ResponseEntity.ok().body(new MessageDTO("ok"));

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }

  }

  @PostMapping(path = "/{login}/role/remove")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> remove(@PathVariable("login") String name, RoleDTO roleDTO) {
    var validator = new UserValidator().validateLogin(name).validateRole(roleDTO);

    try {
      if (validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
          new Exception(validator.getDescription()));
      }

      try {
        userService.removeRole(name, roleDTO.getRole());
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "remove failed", ex);
      }

      return ResponseEntity.ok().body(new MessageDTO("ok"));

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  @GetMapping(path = "/{login}/role")
  public ResponseEntity<?> remove(@PathVariable("login") String name) {
    var validator = new UserValidator().validateLogin(name);

    try {
      if (validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
          new Exception(validator.getDescription()));
      }

      try {
        return ResponseEntity.ok().body(userService.getRoles(name));
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, "get failed", ex);
      }

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  private class UserValidator extends Validator {
    public UserValidator validateLogin(String login) {
      if (login == null || login.isEmpty()) {
        this.addViolation("login", "user login in not set or empty");
      }
      return this;
    }

    public UserValidator validateRole(RoleDTO req) {
      if (req.getRole() == null) {
        this.addViolation("role", "role is not set or empty");
      }
      return this;
    }
  }

}
