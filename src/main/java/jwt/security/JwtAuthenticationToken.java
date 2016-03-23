package jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private Jwt<Header, Claims> jwt;
  private String token;

  public JwtAuthenticationToken(String token) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.token = token;
  }

  public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Jwt<Header, Claims> jwt, String token) {
    super(authorities);
    this.token = token;
    this.jwt = jwt;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return "N/A";
  }

  @Override
  public Object getPrincipal() {
    return jwt != null && jwt.getBody() != null ? jwt.getBody().get("username") : null;
  }

  public String getToken() {
    return token;
  }

  public Jwt<Header, Claims> getJwt() {
    return jwt;
  }
}
