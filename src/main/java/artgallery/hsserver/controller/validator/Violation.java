package artgallery.hsserver.controller.validator;

import lombok.AllArgsConstructor;

/**
 * Violation
 */
@AllArgsConstructor
public class Violation {
  public String field;
  public String violation; // violation description
}
