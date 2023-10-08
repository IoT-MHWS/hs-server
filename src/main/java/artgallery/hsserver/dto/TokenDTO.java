package artgallery.hsserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * TokenDTO
 */
@Data
@Getter
@Setter
@AllArgsConstructor
public class TokenDTO {
  private String jwtToken;
  private String refreshToken;
}
