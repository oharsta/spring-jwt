package jwt.user;

import jwt.domain.User;

import java.io.IOException;
import java.util.Optional;

public interface UserManager {

  Optional<User> loadUser(String username, String credentials) throws IOException;

}
