package artgallery.hsserver.exception;

public class TicketDoesNotExistException extends Exception {
  public TicketDoesNotExistException(Long id) {
    super(String.format(("ticket %s does not exist"), id));
  }
}
