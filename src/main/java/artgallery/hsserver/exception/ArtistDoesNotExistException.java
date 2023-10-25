package artgallery.hsserver.exception;

public class ArtistDoesNotExistException extends DoesNotExistException {
  public ArtistDoesNotExistException(Long id) {
    super(String.format(("artist %s does not exist"), id));
  }
}
