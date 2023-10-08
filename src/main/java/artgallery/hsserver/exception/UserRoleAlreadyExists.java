package artgallery.hsserver.exception;

import artgallery.hsserver.model.Role;

/**
 * RoleAlreadyExists
 */
public class UserRoleAlreadyExists extends Exception {
  public UserRoleAlreadyExists(String login, Role role) {
    super(String.format(("role %s for user %s already exists"), role.name(), login));
  }

}
