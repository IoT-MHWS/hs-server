package artgallery.hsserver.configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import artgallery.hsserver.model.UserEntity;
import artgallery.hsserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * ApplicationConfiguration
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

  private final UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      UserEntity userEntity = userRepository.findByLogin(username)
          .orElseThrow(() -> new UsernameNotFoundException("user not found"));

      return new CustomUserDetails(userEntity);
    };
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private static class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> grantedAuthorities;

    public CustomUserDetails(UserEntity userEntity) {
      username = userEntity.getLogin();
      password = userEntity.getPassword();
      grantedAuthorities = userEntity.getRoles().stream()
          .map(role -> new SimpleGrantedAuthority(role.getName()))
          .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return grantedAuthorities;
    }

    @Override
    public String getPassword() {
      return password;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return true;
    }
  }

}
