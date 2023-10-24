package artgallery.hsserver.exception;

public class UserAlreadyExists extends DatabaseConflictException {
  public UserAlreadyExists(String login) {
    super(String.format(("user with login '%s' already exists"), login));
  }
}
