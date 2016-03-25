package jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jwt.user.UserRepository;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.data.mongodb.core.query.Criteria.where;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest(randomPort = true, value = {"spring.data.mongodb.database=local"})
public class AbstractApplicationTest {

  protected HttpHeaders headers;

  @Value("${local.server.port}")
  protected int port;

  @Value("${secret.key}")
  protected String secretKey;

  @Autowired
  protected MongoTemplate mongoTemplate;

  @Before
  public void before() {
    headers = new PrePopulatedJsonHttpHeaders();
    mongoTemplate.dropCollection("users");
    Arrays.asList("mongo/john.doe.json", "mongo/mary.doe.json").forEach(this::saveJson);
  }

  private void saveJson(String path) {
    try {
      mongoTemplate.save(IOUtils.toString(new ClassPathResource(path).getInputStream()), "users");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
