package artgallery.hsserver.model;

import javax.persistence.*;

import artgallery.hsserver.enums.Style;
import lombok.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    private UUID id;

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
    @JoinTable(
            name = "gallery_painting",
            joinColumns = @JoinColumn(name = "painting_id"),
            inverseJoinColumns = @JoinColumn(name = "gallery_id")
    )
    private List<GalleryEntity> galleries;

}
