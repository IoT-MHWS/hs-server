package artgallery.hsserver.dto;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;

/**
 * ErrorDTO
 */
@AllArgsConstructor
public class ErrorDTO {
  public HttpStatusCode code;
  public String error;
}
