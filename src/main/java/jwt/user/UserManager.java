package jwt.user;

import java.io.IOException;
import java.util.Optional;

public interface UserManager {

  Optional<String> payloadForUser(String username, String credentials) throws IOException;

}
