package jwt.user;

import jwt.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  Optional<User> findUserByUsername(String username);

  Optional<User> findUserByInvitationHash(String invitationHash);

  default Optional<User> loadUser(String username, String credentials) {
    Optional<User> userOptional = findUserByUsername(username);
    return userOptional.map(user -> user.isActive() && passwordEncoder.matches(credentials, user.getPassword()) ? user : null);
  }
}
