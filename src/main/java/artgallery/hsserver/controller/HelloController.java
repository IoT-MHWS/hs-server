package artgallery.hsserver.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;

/**
 * HelloController
 */
@RestController("/api/v1/hello")
public class HelloController {

  @GetMapping
  @PreAuthorize("hasRole('PUBLIC')")
  public String get() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities().toString();
  }
}
