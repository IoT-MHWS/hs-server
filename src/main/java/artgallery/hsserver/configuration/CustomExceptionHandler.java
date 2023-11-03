package artgallery.hsserver.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import artgallery.hsserver.controller.ApiError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

/**
 * CustomExceptionHalder
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({ ExpiredJwtException.class, SignatureException.class })
  @ResponseBody
  public ResponseEntity<ApiError> handleJwtExpiredException(Exception ex) {
    ApiError re = new ApiError(HttpStatus.UNAUTHORIZED,
        "Authentication failed at jwt handling", ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
  }

  @ExceptionHandler({ InsufficientAuthenticationException.class, AccessDeniedException.class })
  @ResponseBody
  public ResponseEntity<ApiError> handleAuthenticationException(Exception ex) {
    var re = new ApiError(HttpStatus.UNAUTHORIZED,
        "Authentication failed", ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
  }

}
