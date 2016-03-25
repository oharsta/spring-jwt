package jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class ApplicationTest extends AbstractApplicationTest {

  @Test
  public void testAdminJwtToken() {
    String token = getToken(Optional.of("John Doe"), "secret");

    boolean signed = Jwts.parser().isSigned(token);
    assertTrue(signed);

    Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    assertEquals("John Doe", jws.getBody().get("username"));
    assertNull(jws.getBody().get("password"));

    assertUserName(token, 200, "John Doe", "/admin/user");
  }

  @Test
  public void testNoAdminRights() {
    String token = getToken(Optional.of("Mary Doe"), "secret");
    assertUserName(token, 200, "Mary Doe", "/user");
    assertUserName(token, 403, "N/A", "/admin/user");
  }

  @Test
  public void testNotValidUser() {
    getToken(Optional.of("unknown"), "N/A", 403);
  }

  @Test
  public void testNotValidPassword() {
    getToken(Optional.of("John Doe"), "wrong", 403);
  }

  @Test
  public void testInvalidToken() {
    assertUserName("invalid.token", 403, "Mary Doe", "/user");
  }

  private void assertUserName(String token, int expectedStatus, String expectedUserName, String path) {
    headers.add("X-AUTH-TOKEN", token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<Map> adminResponse = new TestRestTemplate().exchange("http://localhost:" + port + path, HttpMethod.GET, entity, Map.class);
    assertEquals(expectedStatus, adminResponse.getStatusCode().value());
    if (expectedStatus == 200) {
      assertEquals(expectedUserName, adminResponse.getBody().get("user"));
    }
  }

  private String getToken(Optional<String> username, String credentials) {
    return getToken(username, credentials, 200);
  }

  private String getToken(Optional<String> username, String credentials, int expectedStatus) {
    HttpEntity<String> entity = new HttpEntity<>(headers);
    RestTemplate template = username.isPresent() ? new TestRestTemplate(username.get(), credentials) : new TestRestTemplate();
    ResponseEntity<String> response = template.exchange("http://localhost:" + port + "/token", HttpMethod.POST, entity, String.class);
    assertEquals(expectedStatus, response.getStatusCode().value());
    return response.getBody();
  }

}
