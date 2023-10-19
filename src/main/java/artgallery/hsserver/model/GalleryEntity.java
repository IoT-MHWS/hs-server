package artgallery.hsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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

  @NotNull(message = "name must be not null")
  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @OneToMany(mappedBy = "gallery")
  @ToString.Exclude
  private List<ExhibitionEntity> exhibitions;

  @OneToMany(mappedBy = "gallery")
  @ToString.Exclude
  private List<GalleryPaintingEntity> galleryPaintings;
}
