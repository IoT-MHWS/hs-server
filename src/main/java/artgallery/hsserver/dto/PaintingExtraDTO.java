package artgallery.hsserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaintingExtraDTO {
  private long id;
  private String name;
  private Integer yearOfCreation;
  private Long artistId;
  private String description;
}
