package jwt.web;

import jwt.security.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Collections.singletonMap;

@RestController
@RequestMapping(headers = {"Content-Type=application/json"}, produces = {"application/json"})
public class UserController {

  @RequestMapping(method = RequestMethod.POST, value = "/token")
  public String token(Authentication authentication) {
    return ((JwtAuthenticationToken) authentication).getToken();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/user")
  public Map<String, String> user(Authentication authentication) {
    return singletonMap("user", authentication.getName());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/admin/user")
  public Map<String, String> admin(Authentication authentication) {
    return singletonMap("user", authentication.getName());
  }

}
