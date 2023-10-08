package artgallery.hsserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserDoesNotExistException extends Exception {
  public UserDoesNotExistException(String login) {
    super(String.format(("user %s does not exist"), login));
  }
}

