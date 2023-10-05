package artgallery.hsserver.model;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    String name;

    @NotNull(message = "date must be not null")
    @Column(name = "start_date")
    private Date startDate;

    @NotNull(message = "date must be not null")
    @Column(name = "end_date")
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id", referencedColumnName="id")
    private GalleryEntity gallery;
}
