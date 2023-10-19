package artgallery.hsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "`order`")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull(message = "date must be not null")
  @Column(name = "date")
  private Date date;

  @NotNull(message = "user must be not null")
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @OneToMany(mappedBy = "order")
  @ToString.Exclude
  private List<TicketEntity> ticketEntities;
}
