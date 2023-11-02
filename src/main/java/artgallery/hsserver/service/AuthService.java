package artgallery.hsserver.service;

import artgallery.hsserver.configuration.CustomUserDetails;
import artgallery.hsserver.configuration.JwtService;
import artgallery.hsserver.dto.TokenDTO;
import artgallery.hsserver.dto.UserDTO;
import artgallery.hsserver.exception.UserDoesNotExistException;
import artgallery.hsserver.model.UserEntity;
import artgallery.hsserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


/**
 * AuthService
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  public static final String JWT_HEADER = "Authorization";
  public static final String JWT_PREFIX = "Bearer ";

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;


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
