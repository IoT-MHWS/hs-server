package artgallery.hsserver.controller;

import artgallery.hsserver.controller.validator.Validator;
import artgallery.hsserver.dto.MessageDTO;
import artgallery.hsserver.dto.RoleDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.service.AuthService;
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
@RequestMapping(path = "/api/v1/users", produces = { "application/json", "application/xml" })
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  @PostMapping(path = "/{login}/roles/add")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
  public ResponseEntity<?> addRole(@PathVariable("login") String name, @RequestBody RoleDTO roleDTO) {
    var validator = new UserValidator().validateLogin(name).validateRole(roleDTO);

    return ControllerExecutor.execute(validator, () -> {
      userService.addRole(name, roleDTO.getRole());
      return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("ok"));
    });
  }

  @PostMapping(path = "/{login}/roles/remove")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
  public ResponseEntity<?> removeRole(@PathVariable("login") String name, @RequestBody RoleDTO roleDTO) {
    var validator = new UserValidator().validateLogin(name).validateRole(roleDTO);

    return ControllerExecutor.execute(validator, () -> {
      userService.removeRole(name, roleDTO.getRole());
      return ResponseEntity.noContent().build();
    });
  }

  @GetMapping(path = "/{login}/roles")
  @PreAuthorize("hasRole('PUBLIC')")
  public ResponseEntity<?> listUserRoles(@PathVariable("login") String name) {
    var validator = new UserValidator().validateLogin(name);

    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.ok().body(userService.getRoles(name));
    });
  }

  @PostMapping(path = "/create")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public ResponseEntity<?> register(@RequestBody UserDTO req) {
    var validator = new UserValidator().validatePassword(req);

    return ControllerExecutor.execute(validator, () -> {
      return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(req));
    });
  }

  private static class UserValidator extends Validator {
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
    public UserValidator validatePassword(UserDTO req) {
      if (req.getPassword() == null || req.getPassword().isEmpty()) {
        this.addViolation("password", "user password in not set or empty");
      }
      return this;
    }
  }

}
