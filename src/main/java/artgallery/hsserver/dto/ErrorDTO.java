package artgallery.hsserver.dto;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;

/**
 * ErrorDTO
 */
@AllArgsConstructor
public class ErrorDTO {
  public HttpStatusCode code;
  public String error;
}
