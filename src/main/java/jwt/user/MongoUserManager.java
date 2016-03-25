package jwt.user;

import jwt.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Optional;

public class MongoUserManager implements UserManager {

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private final UserRepository userRepository;

  public MongoUserManager(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> loadUser(String username, String credentials) throws IOException {
    Optional<User> userOptional = userRepository.findUserByUsername(username);
    if (userOptional.isPresent() && passwordEncoder.matches(credentials, userOptional.get().getPassword())) {
      return userOptional;
    }
    return Optional.empty();
  }

}
