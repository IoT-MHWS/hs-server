package artgallery.hsserver.model;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "login", nullable = false, unique = true, length = 32)
    @NotBlank(message = "must be not null")
    private String login;

    @Column(name = "password")
    @NotBlank(message = "must be not null")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @NotNull(message = "must have role_id")
    private RoleEntity role;

}
