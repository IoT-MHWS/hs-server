package artgallery.hsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "ticket")
public class TicketEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @Column(name = "description")
  private String description;

  @NotNull(message = "price must be not null")
  @Column(name = "price")
  private Integer price;

  @ManyToOne()
  @JoinColumn(name = "exhibition_id")
  @ToString.Exclude
  private ExhibitionEntity exhibition;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @ToString.Exclude
  private OrderEntity order;
}
