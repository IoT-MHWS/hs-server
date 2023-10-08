package artgallery.hsserver.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "role")
public class RoleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToMany(mappedBy = "roles")
  private Set<UserEntity> users = new HashSet<>();

  @Column(name = "name")
  @NotBlank(message = "must be not null")
  @Size(min = 2, max = 32, message = "role must be [2; 32] long")
  private String name;
}
