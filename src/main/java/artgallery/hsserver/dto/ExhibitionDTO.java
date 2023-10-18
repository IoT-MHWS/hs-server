package artgallery.hsserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionDTO {
  private long id;
  private String name;
  private Date startDate;
  private Date endDate;
  private Long galleryId;
}
