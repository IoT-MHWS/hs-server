package artgallery.hsserver.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private UUID id;

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
