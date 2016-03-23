package jwt.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jwt.security.InvalidAuthenticationException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Map;

public class MockUserManager implements UserManager {

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SuppressWarnings("unchecked")
  public String validateUser(String username, String credentials) {
    String payload = getUserPayload(username);
    try {
      Map<String, Object> user = objectMapper.readValue(payload, Map.class);
      if (passwordEncoder.matches(credentials, (String) user.get("password"))) {
        return payload;
      } else {
        throw new InvalidAuthenticationException("Acces denied");
      }
    } catch (IOException e) {
      throw new InvalidAuthenticationException("Access denied", e);
    }
  }

  private String getUserPayload(String username) throws AuthenticationException {
    try {
      return IOUtils.toString(new ClassPathResource(String.format("%s.json", username)).getInputStream());
    } catch (IOException e) {
      throw new InvalidAuthenticationException("Access denied", e);
    }
  }

}
