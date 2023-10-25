package artgallery.hsserver.exception;

public class PaintingDoesNotExistException extends DoesNotExistException {
  public PaintingDoesNotExistException (Long id) {
    super(String.format(("painting %s does not exist"), id));
  }
}
