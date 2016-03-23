package jwt;

import io.jsonwebtoken.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest(randomPort = true)
public class ApplicationTest {

  private HttpHeaders headers;

  @Value("${local.server.port}")
  private int port;

  @Value("${secret.key}")
  private String secretKey;

  @Before
  public void before() {
    headers = new PrePopulatedJsonHttpHeaders();
  }

  @Test
  public void testAdminJwtToken() {
    String token = getToken(Optional.of("john.doe"), "secret");

    boolean signed = Jwts.parser().isSigned(token);
    assertTrue(signed);

    Jwt<Header, Claims> jwt = Jwts.parser().setSigningKey(secretKey()).parse(token);
    assertEquals("John Doe", jwt.getBody().get("username"));

    assertUserName(token, 200, "John Doe", "/admin/user");
  }

  @Test
  public void testNoAdminRights() {
    String token = getToken(Optional.of("mary.doe"), "secret");
    assertUserName(token, 200, "Mary Doe", "/user");
    assertUserName(token, 403, "N/A", "/admin/user");
  }

  @Test
  public void testNotValidUser() {
    getToken(Optional.of("unknown"), "N/A", 403);
  }

  @Test
  public void testNotValidPassword() {
    getToken(Optional.of("john.doe"), "wrong", 403);
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

  private SecretKey secretKey() {
    return new SecretKeySpec(secretKey.getBytes(), "AES");
  }

}
