package artgallery.hsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "gallery")
public class GalleryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull(message = "date must be not null")
  @Column(name = "date")
  private Date date;

  @ManyToMany(mappedBy = "galleries")
  @Fetch(FetchMode.SUBSELECT)
  @ToString.Exclude
  private List<PaintingEntity> paintings;

  @OneToMany(mappedBy = "gallery")
  @ToString.Exclude
  private List<ExhibitionEntity> exhibitions;

}
