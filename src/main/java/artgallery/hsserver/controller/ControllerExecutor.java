package artgallery.hsserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import artgallery.hsserver.controller.validator.Validator;

class ControllerExecutor {

  public static ResponseEntity<?> execute(Validator validator, ControllerRunner controllerFunc, String serviceErrMsg) {
    try {
      if (validator != null && validator.hasViolations()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "validation isn't passed",
            new Exception(validator.getDescription()));
      }

      try {
        return controllerFunc.run();
      } catch (Exception ex) {
        throw new ApiException(HttpStatus.CONFLICT, serviceErrMsg, ex);
      }

    } catch (ApiException e) {
      return ResponseEntity.status(e.get().getStatus()).body(e.get());
    }
  }

  public interface ControllerRunner {
    public ResponseEntity<?> run() throws Exception;
  }
}
