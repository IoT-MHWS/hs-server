package artgallery.hsserver.model;

import java.util.List;

import artgallery.hsserver.enums.Style;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private long id;

  @Column(name = "year_of_creation")
  Integer yearOfCreation;

  @NotNull(message = "must be not null")
  @Enumerated(EnumType.STRING)
  @Column(name = "style")
  private Style style;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false)
  private ArtistEntity artistEntity;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "gallery_painting", joinColumns = @JoinColumn(name = "painting_id"), inverseJoinColumns = @JoinColumn(name = "gallery_id"))
  private List<GalleryEntity> galleries;

}
