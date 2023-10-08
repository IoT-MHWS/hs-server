package artgallery.hsserver.exception;

import artgallery.hsserver.model.Role;

/**
 * RoleDoesNotExistException
 */
public class RoleDoesNotExistException extends Exception {
  public RoleDoesNotExistException(Role role) {
    super(String.format(("role %s does not exist"), role.name()));
  }
}
