package artgallery.hsserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfiguration
 */
@Configuration
// @EnableWebSecurity
public class SecurityConfiguration {

  // private final String CURRENT_API_PREFIX = "/api/v1";

  // @Bean
  // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  //   http
  //       .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
  //       .authorizeHttpRequests((requests) -> requests
  //           .requestMatchers("/",
  //               CURRENT_API_PREFIX + "/user/register",
  //               CURRENT_API_PREFIX + "/user/login"
  //               )
  //           .permitAll()
  //           .anyRequest().authenticated()
  //       );

  //   return http.build();
  // }

  // // @Bean


}
