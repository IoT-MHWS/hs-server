package artgallery.hsserver.dto;

import lombok.*;

/**
 * TokenDTO
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
  private String jwtToken;
  private String refreshToken;
}
