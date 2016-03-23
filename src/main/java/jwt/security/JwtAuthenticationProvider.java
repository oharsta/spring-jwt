package jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jwt.user.UserManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final String secretKey;
  private final UserManager userManager;

  public JwtAuthenticationProvider(String secretKey, UserManager userManager) {
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
      String token = authentication instanceof JwtAuthenticationToken ?
          ((JwtAuthenticationToken) authentication).getToken() : getToken(authentication);
      return getJwtAuthentication(token);
    } catch (RuntimeException e) {
      throw new InvalidAuthenticationException("Access denied", e);
    }
  }

  @SuppressWarnings("unchecked")
  private Authentication getJwtAuthentication(String token) {
    Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    List<SimpleGrantedAuthority> grantedAuthorities = ((List<Map<String, String>>) claimsJws.getBody().get("roles"))
        .stream().map(m -> new SimpleGrantedAuthority(m.get("role"))).collect(toList());
    return new JwtAuthenticationToken(grantedAuthorities, claimsJws.getBody().get("username", String.class), token);
  }

  private String getToken(Authentication authentication) {
    String name = authentication.getName();
    String credentials = (String) authentication.getCredentials();
    try {
      String payLoad = userManager.payloadForUser(name, credentials)
          .orElseThrow(() -> new InvalidAuthenticationException("Access denied"));
      return Jwts.builder().setPayload(payLoad).signWith(SignatureAlgorithm.HS512, secretKey).compact();
    } catch (IOException e) {
      throw new InvalidAuthenticationException("Access denied",e);
    }
  }

}
