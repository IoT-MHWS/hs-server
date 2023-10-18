package artgallery.hsserver.dto;

import artgallery.hsserver.model.Style;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDTO {
  private long id;
  private String name;
  private Integer yearOfBirth;
  private String bio;
  private Style style;
}
