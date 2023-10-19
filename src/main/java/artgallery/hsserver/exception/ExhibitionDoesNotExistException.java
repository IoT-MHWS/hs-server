package artgallery.hsserver.exception;

public class ExhibitionDoesNotExistException extends Exception {
  public ExhibitionDoesNotExistException(Long id) {
    super(String.format(("exhibition %s does not exist"), id));
  }
}


