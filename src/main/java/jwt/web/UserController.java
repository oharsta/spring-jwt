package jwt.web;

import jwt.domain.AcceptInvitation;
import jwt.domain.InvitationNotFoundException;
import jwt.domain.User;
import jwt.mail.MailBox;
import jwt.security.JwtAuthenticationToken;
import jwt.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.*;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Arrays.asList;
import static java.util.Base64.getEncoder;
import static java.util.Collections.singletonMap;
import static jwt.user.UserRepository.passwordEncoder;

@RestController
@RequestMapping(headers = {"Content-Type=application/json"}, produces = {"application/json"})
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MailBox mailBox;

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

  @RequestMapping(method = RequestMethod.POST, value = "/owner/user")
  public User createUser(@Validated @RequestBody User user) throws UnsupportedEncodingException {
    return doCreateUser(user, "USER");
  }

  @RequestMapping(method = RequestMethod.POST, value = "/admin/user")
  public User createOwner(@Validated @RequestBody User user) throws UnsupportedEncodingException {
    return doCreateUser(user, "USER", "OWNER");
  }

  @RequestMapping(method = RequestMethod.POST, value = "/invitation/accept")
  public void invitationAccept(@Validated @RequestBody AcceptInvitation acceptInvitation) {
    User user = userRepository.findUserByInvitationHash(acceptInvitation.getInvitationHash()).orElseThrow(InvitationNotFoundException::new);
    user.activate(passwordEncoder.encode(acceptInvitation.getPassword()));
    userRepository.save(user);
  }

  private User doCreateUser(User user, String... roles) throws UnsupportedEncodingException {
    Optional<User> userByUsername = userRepository.findUserByUsername(user.getUsername());
    if (userByUsername.isPresent()) {
      throw new DuplicateKeyException(format("User with username %s already exists", user.getUsername()));
    }
    User saved = userRepository.save(new User(user.getUsername(), user.getEmail(), user.getOrganization(), asList(roles), generateInvitationHash()));
    mailBox.sendInvitationMail(saved);
    return saved;
  }

  private String generateInvitationHash() throws UnsupportedEncodingException {
    //this does not happen ofter, no need for performance optimization
    Random secureRandom = new SecureRandom();
    byte[] aesKey = new byte[512];
    secureRandom.nextBytes(aesKey);
    return encode(getEncoder().encodeToString(aesKey), "UTF-8");
  }

}
