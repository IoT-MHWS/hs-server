package artgallery.hsserver.exception;

public class ExhibitionDoesNotExistException extends DoesNotExistException {
  public ExhibitionDoesNotExistException(Long id) {
    super(String.format(("exhibition %s does not exist"), id));
  }
}


