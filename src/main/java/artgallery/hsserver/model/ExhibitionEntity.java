package artgallery.hsserver.model;

import lombok.*;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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
