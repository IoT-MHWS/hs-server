package artgallery.hsserver.exception;

import artgallery.hsserver.model.Role;

/**
 * UserRoleDoesNotExist
 */
public class UserRoleDoesNotExist extends Exception {
  public UserRoleDoesNotExist(String login, Role role) {
    super(String.format(("role %s for user %s does not exists"), role.name(), login));
  }

}
