package jwt.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jwt.domain.JsonMapper;
import jwt.domain.User;
import jwt.user.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JwtAuthenticationProvider implements AuthenticationProvider, JsonMapper {

  private final String secretKey;
  private final UserRepository userRepository;

  public JwtAuthenticationProvider(String secretKey, UserRepository userRepository) {
    this.secretKey = secretKey;
    this.userRepository = userRepository;
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
          getJwtAuthentication(getUser(authentication));
    } catch (RuntimeException | IOException e) {
      throw new InvalidAuthenticationException("Access denied", e);
    }
  }

  private Authentication getJwtAuthentication(User user) throws JsonProcessingException {
    user.erasePassword();
    String token = Jwts.builder().setPayload(mapper.writeValueAsString(user)).signWith(SignatureAlgorithm.HS512, secretKey).compact();
    return new JwtAuthenticationToken(grantedAuthorities(user.getRoles()), user.getId(), user.getUsername(), token);
  }

  @SuppressWarnings("unchecked")
  private Authentication getJwtAuthentication(String token) {
    //this will fail if the token is tampered with
    Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    List<SimpleGrantedAuthority> grantedAuthorities = grantedAuthorities((List<String>) claimsJws.getBody().get("roles"));
    return new JwtAuthenticationToken(
        grantedAuthorities,
        claimsJws.getBody().get("id", String.class),
        claimsJws.getBody().get("username", String.class),
        token);
  }

  private List<SimpleGrantedAuthority> grantedAuthorities(List<String> roles) {
    return roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
  }

  private User getUser(Authentication authentication) throws IOException {
    return userRepository.loadUser(authentication.getName(), (String) authentication.getCredentials())
        .orElseThrow(() -> new InvalidAuthenticationException("Access denied"));
  }

}
