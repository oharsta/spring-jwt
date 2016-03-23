package jwt.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jwt.security.InvalidAuthenticationException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class MockUserManager implements UserManager {

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SuppressWarnings("unchecked")
  public Optional<String> payloadForUser(String username, String credentials) throws IOException {
    String payload = IOUtils.toString(new ClassPathResource(String.format("%s.json", username)).getInputStream());
    Map<String, Object> user = objectMapper.readValue(payload, Map.class);
    if (passwordEncoder.matches(credentials, (String) user.get("password"))) {
      user.remove("password");
      return Optional.of(objectMapper.writeValueAsString(user));
    }
    return Optional.empty();
  }

}
