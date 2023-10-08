package artgallery.hsserver.configuration;

import artgallery.hsserver.model.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
  private final String username;
  private final String password;
  private final List<SimpleGrantedAuthority> grantedAuthorities;

  public CustomUserDetails(UserEntity userEntity) {
    username = userEntity.getLogin();
    password = userEntity.getPassword();
    grantedAuthorities = userEntity.getRoles().stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
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
