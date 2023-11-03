package artgallery.hsserver.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * SecurityConfiguration
 */
@Configuration
@EnableWebSecurity()
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final String[] WHITE_LIST_URLS = {
      "/api-docs",
      "/api-docs/**",
      "/swagger-ui",
      "/swagger-ui/**",
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;
  private final CustomAuthEntryPoint customAuthEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .authorizeHttpRequests((req) -> req
            .requestMatchers(WHITE_LIST_URLS).permitAll()
            .requestMatchers("/api/v1/auth/login").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .disable())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthEntryPoint));

    return http.build();
  }

}
