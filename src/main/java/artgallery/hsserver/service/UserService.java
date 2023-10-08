package artgallery.hsserver.service;

import artgallery.hsserver.dto.RoleDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.exception.UserRoleAlreadyExists;
import artgallery.hsserver.exception.UserRoleDoesNotExist;
import artgallery.hsserver.model.Role;
import artgallery.hsserver.model.RoleEntity;
import artgallery.hsserver.model.UserEntity;
import artgallery.hsserver.repository.RoleRepository;
import artgallery.hsserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public void addRole(String login, Role role)
    throws UserDoesNotExistException, RoleDoesNotExistException, UserRoleAlreadyExists {
    UserEntity userEntity = userRepository.findByLogin(login)
      .orElseThrow(() -> new UserDoesNotExistException(login));

    RoleEntity roleEntity = roleRepository.findByName(role.name())
      .orElseThrow(() -> new RoleDoesNotExistException(role));

    if (userEntity.getRoles().contains(roleEntity)) {
      throw new UserRoleAlreadyExists(login, role);
    }

    userEntity.getRoles().add(roleEntity);
    userRepository.save(userEntity);
  }

  public void removeRole(String login, Role role)
    throws UserDoesNotExistException, RoleDoesNotExistException, UserRoleDoesNotExist {
    UserEntity userEntity = userRepository.findByLogin(login)
      .orElseThrow(() -> new UserDoesNotExistException(login));

    RoleEntity roleEntity = roleRepository.findByName(role.name())
      .orElseThrow(() -> new RoleDoesNotExistException(role));

    if (!userEntity.getRoles().contains(roleEntity)) {
      throw new UserRoleDoesNotExist(login, role);
    }

    userEntity.getRoles().remove(roleEntity);
    userRepository.save(userEntity);
  }

  public List<RoleDTO> getRoles(String login) throws UserDoesNotExistException {
    UserEntity userEntity = userRepository.findByLogin(login)
      .orElseThrow(() -> new UserDoesNotExistException(login));

    return userEntity.getRoles().stream()
      .map(role -> new RoleDTO(Role.valueOf(role.getName())))
      .collect(Collectors.toList());
  }
}
