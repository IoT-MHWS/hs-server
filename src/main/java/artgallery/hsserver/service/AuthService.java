package artgallery.hsserver.service;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import artgallery.hsserver.configuration.CustomUserDetails;
import artgallery.hsserver.configuration.JwtService;
import artgallery.hsserver.dto.TokenDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.RoleDoesNotExistException;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.model.Role;
import artgallery.hsserver.model.RoleEntity;
import artgallery.hsserver.model.UserEntity;
import artgallery.hsserver.repository.RoleRepository;
import artgallery.hsserver.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * AuthService
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  public static final String JWT_HEADER = "Authorization";
  public static final String JWT_PREFIX = "Bearer ";

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  public void register(UserDTO req) throws RoleDoesNotExistException {
    var userEntity = UserEntity.builder()
        .login(req.getLogin())
        .password(passwordEncoder.encode(req.getPassword()))
        .build();

    var roleEntity = roleRepository.findByName(Role.PUBLIC.name())
        .orElseThrow(() -> new RoleDoesNotExistException(Role.PUBLIC));

    userEntity.getRoles().add(roleEntity);

    userRepository.save(userEntity);
  }

  public TokenDTO login(UserDTO req) throws UserDoesNotExistException {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        req.getLogin(), req.getPassword()));

    var userEntity = userRepository.findByLogin(req.getLogin())
        .orElseThrow(() -> new UserDoesNotExistException(req.getLogin()));

    var customUserDetails = new CustomUserDetails(userEntity);

    var jwtToken = jwtService.generateToken(customUserDetails);
    var refreshToken = jwtService.generateRefreshToken(customUserDetails);

    return new TokenDTO(jwtToken, refreshToken);
  }

  public TokenDTO refreshToken(HttpHeaders reqHeaders) throws Exception {
    final String authHeader = reqHeaders.getOrEmpty(JWT_HEADER).stream().findFirst().orElse(null);
    final String refreshToken;
    final String login;

    if (authHeader == null || !authHeader.startsWith(JWT_PREFIX)) {
      throw new Exception("jwt header is not present");
    }

    refreshToken = authHeader.substring(JWT_PREFIX.length());
    login = jwtService.extractUsername(refreshToken);

    if (login != null) {
      UserEntity userEntity = userRepository.findByLogin(login)
          .orElseThrow(() -> new UserDoesNotExistException(login));
      var userDetails = new CustomUserDetails(userEntity);

      if (jwtService.isTokenValid(refreshToken, userDetails)) {
        var accessToken = jwtService.generateToken(userDetails);
        return new TokenDTO(accessToken, refreshToken);
      }
      throw new Exception("token is invalid");
    }
    throw new Exception("not found");
  }

}
