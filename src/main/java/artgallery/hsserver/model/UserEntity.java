package artgallery.hsserver.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

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
  private long id;

  @Column(name = "login", nullable = false, unique = true, length = 32)
  @NotBlank(message = "must be not null")
  private String login;

  @Column(name = "password")
  @NotBlank(message = "must be not null")
  private String password;

  @ManyToMany(cascade = { CascadeType.ALL })
  @JoinTable(
    name = "user_role",
    joinColumns = { @JoinColumn(name = "user_id") },
    inverseJoinColumns = { @JoinColumn(name = "role_id") }
  )
  private Set<RoleEntity> roles = new HashSet<>();

}
