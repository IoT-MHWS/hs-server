package artgallery.hsserver.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * JwtLogoutHandler
 */
// INFO: project implementation of JWT doesn't shore info about expired tokens
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  public static final String JWT_HEADER = "Authorization";
  public static final String JWT_PREFIX = "Bearer ";

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader(JWT_HEADER);

    if (authHeader == null || !authHeader.startsWith(JWT_PREFIX)) {
      return;
    }

    // final String jwtToken = authHeader.substring(JWT_PREFIX.length());
    // find token using jwtService
    // revoke + expire + save token in repository
  }

}
