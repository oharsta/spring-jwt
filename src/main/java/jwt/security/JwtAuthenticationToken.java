package jwt.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private String userId;
  private String name;
  private String token;

  public JwtAuthenticationToken(String token) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.token = token;
  }

  public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String userId, String name, String token) {
    super(authorities);
    this.userId = userId;
    this.token = token;
    this.name = name;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return "N/A";
  }

  @Override
  public Object getPrincipal() {
    return name;
  }

  public String getToken() {
    return token;
  }

  public String getUserId() {
    return userId;
  }
}
