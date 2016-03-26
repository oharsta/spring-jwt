package jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jwt.domain.Invitation;
import jwt.domain.User;
import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.HttpMethod.POST;

public class UserControllerTest extends AbstractApplicationTest {

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

  @Test
  public void testCreateOwner() {
    String token = getToken(Optional.of("John Doe"), "secret");
    doTestCreateUser(token, "/admin/user", "USER", "OWNER");
  }

  @Test
  public void testCreateUserExistingName() {
    String token = getToken(Optional.of("John Doe"), "secret");

    User user = new User("Pete Doe", "o@t.com", "org", emptyList());
    ResponseEntity<Map> response = exchangeWithToken(token, "/admin/user", Optional.of(user), POST);

    assertEquals(500, response.getStatusCode().value());
    assertEquals(DuplicateKeyException.class.getName(), response.getBody().get("exception"));
  }

  @Test
  public void testInactiveUserToken() {
    getToken(Optional.of("Inactive Doe"), "secret", 403);
  }

  @Test
  public void testCreateUser() {
    String token = getToken(Optional.of("Pete Doe"), "secret");
    Map user = doTestCreateUser(token, "/owner/user", "USER");

    //activate the user
    Query query = query(Criteria.where("_id").is(user.get("id")));
    query.fields().include("invitationHash");
    Map map = mongoTemplate.findOne(query, Map.class, "users");

    ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "invitation/accept", POST,
        new HttpEntity(new Invitation((String) map.get("invitationHash"), "secret"), headers), Void.class);
    assertEquals(200, response.getStatusCode().value());

    User activatedUser = mongoTemplate.findOne(query(Criteria.where("_id").is(user.get("id"))), User.class);
    assertTrue(activatedUser.isActive());
  }

  private Map doTestCreateUser(String token, String path, String... roles) {
    User user = new User(UUID.randomUUID().toString(), "oharsta@zilverline.com", "organization", emptyList());
    ResponseEntity<Map> response = exchangeWithToken(token, path, Optional.of(user), POST);

    assertEquals(200, response.getStatusCode().value());

    Map body = response.getBody();

    assertEquals(false, body.get("active"));
    assertNotNull(body.get("id"));
    assertEquals(user.getEmail(), body.get("email"));
    assertEquals(Arrays.asList(roles), body.get("roles"));

    return body;
  }

  private void assertUserName(String token, int expectedStatus, String expectedUserName, String path) {
    ResponseEntity<Map> response = exchangeWithToken(token, path, Optional.empty(), HttpMethod.GET);
    assertEquals(expectedStatus, response.getStatusCode().value());
    if (expectedStatus == 200) {
      assertEquals(expectedUserName, response.getBody().get("user"));
    }
  }

  private ResponseEntity<Map> exchangeWithToken(String token, String path, Optional<Object> body, HttpMethod httpMethod) {
    headers.add("X-AUTH-TOKEN", token);
    HttpEntity entity = body.isPresent() ? new HttpEntity(body.get(), headers) : new HttpEntity(headers);
    return restTemplate.exchange("http://localhost:" + port + path, httpMethod, entity, Map.class);
  }

}
