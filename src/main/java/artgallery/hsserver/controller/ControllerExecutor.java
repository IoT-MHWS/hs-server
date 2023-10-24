package artgallery.hsserver.controller;

import artgallery.hsserver.exception.DatabaseConflictException;
import artgallery.hsserver.exception.DoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import artgallery.hsserver.controller.validator.Validator;

class ControllerExecutor {

  public static ResponseEntity<?> execute(Validator validator, ControllerRunner controllerFunc) {
    try {
      if (validator != null && validator.hasViolations()) {
        throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "validation isn't passed",
            new Exception(validator.getDescription()));
      }

      try {
        return controllerFunc.run();
      } catch (DoesNotExistException ex) {
        throw new ApiException(HttpStatus.NOT_FOUND, ex.getMessage());
      } catch (DatabaseConflictException ex) {
        throw new ApiException(HttpStatus.CONFLICT, ex.getMessage());
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
      }

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  public interface ControllerRunner {
    public ResponseEntity<?> run() throws Exception;
  }
}
