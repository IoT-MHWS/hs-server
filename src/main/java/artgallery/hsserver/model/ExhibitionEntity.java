package artgallery.hsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "exhibition")
public class ExhibitionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @Column(name = "name")
  String name;

  @NotNull(message = "date must be not null")
  @Column(name = "start_date")
  private Date startDate;

  @NotNull(message = "date must be not null")
  @Column(name = "end_date")
  private Date endDate;

  @ManyToOne()
  @JoinColumn(name = "gallery_id", referencedColumnName = "id")
  private GalleryEntity gallery;
}
