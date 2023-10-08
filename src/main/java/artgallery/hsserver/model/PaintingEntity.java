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

  @Column(name = "year_of_creation")
  Integer yearOfCreation;

  @NotNull(message = "must be not null")
  @Enumerated(EnumType.STRING)
  @Column(name = "style")
  private Style style;

  @ManyToOne()
  @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false)
  private ArtistEntity artistEntity;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "gallery_painting", joinColumns = @JoinColumn(name = "painting_id"), inverseJoinColumns = @JoinColumn(name = "gallery_id"))
  private List<GalleryEntity> galleries;

}
