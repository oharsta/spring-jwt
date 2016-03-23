package jwt.security;

import io.jsonwebtoken.*;
import jwt.user.UserManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final SecretKey secretKey;
  private final UserManager userManager;

  public JwtAuthenticationProvider(SecretKey secretKey, UserManager userManager) {
    this.secretKey = secretKey;
    this.userManager = userManager;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication) ||
        UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      return authentication instanceof JwtAuthenticationToken ?
          getJwtAuthentication(((JwtAuthenticationToken) authentication).getToken()) :
          getJwtAuthentication(getToken(authentication));
    } catch (RuntimeException e) {
      throw new InvalidAuthenticationException("Access denied", e);
    }
  }

  private Authentication getJwtAuthentication(String token) {
    Jwt<Header, Claims> jwt = Jwts.parser().setSigningKey(secretKey).parse(token);
    List<SimpleGrantedAuthority> grantedAuthorities = ((List<Map<String, String>>) jwt.getBody().get("roles")).stream().map(
        m -> new SimpleGrantedAuthority(m.get("role"))).collect(toList());
    return new JwtAuthenticationToken(grantedAuthorities, jwt, token);
  }

  private String getToken(Authentication authentication) {
    String name = authentication.getName();
    String credentials = (String) authentication.getCredentials();
    //do some lookup of the user
    String payLoad = userManager.validateUser(name, credentials);
    return Jwts.builder().setPayload(payLoad).signWith(SignatureAlgorithm.HS512, secretKey).compact();
  }

}
