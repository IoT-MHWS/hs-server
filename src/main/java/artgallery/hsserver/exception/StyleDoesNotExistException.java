package artgallery.hsserver.exception;

public class StyleDoesNotExistException extends Exception {
  public StyleDoesNotExistException(Long id){
    super(String.format(("style %s does not exist"), id));
  }
}
