package artgallery.hsserver.model;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@Table(name = "gallery")
public class GalleryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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
