package artgallery.hsserver.dto;

import artgallery.hsserver.model.Style;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecDTO {
  private Integer yearOfBirth;
  private Style style;
}
