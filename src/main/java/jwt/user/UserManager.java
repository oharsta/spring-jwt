package jwt.user;

public interface UserManager {

  String payloadForUser(String username, String credentials);

}
