package artgallery.hsserver.dto;

import artgallery.hsserver.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RoleDTO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
  private Role role;
}
