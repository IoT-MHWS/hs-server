package artgallery.hsserver.exception;

public class OrderDoesNotExistException extends Exception{
  public OrderDoesNotExistException (Long id) {
    super(String.format(("order %s does not exist"), id));
  }
}
