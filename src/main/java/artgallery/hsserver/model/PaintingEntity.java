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
@Table(name = "painting")
public class PaintingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull(message = "name must be not null")
  @Column(name = "name")
  private String name;

  @Column(name = "year_of_creation")
  private Integer yearOfCreation;

  @ManyToOne()
  @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false)
  private ArtistEntity artistEntity;

  @OneToMany(mappedBy = "painting")
  @ToString.Exclude
  private List<GalleryPaintingEntity> galleryPaintings;

}
