package artgallery.hsserver.exception;

public class LinkAlreadyExistsException extends DatabaseConflictException{
  public LinkAlreadyExistsException(long id0, long id1)  {
    super(String.format(("link between objects '%s' and '%s' already exists"), id0, id1));
  }
}
