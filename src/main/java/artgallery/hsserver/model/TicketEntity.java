package artgallery.hsserver.model;

import jakarta.persistence.*;
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
  String description;

  @Column(name = "price")
  Integer price;

  @ManyToOne()
  @JoinColumn(name = "order_id")
  @ToString.Exclude
  private OrderEntity orderEntity;
}
