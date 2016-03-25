package jwt.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {

  @Id
  private String id;

  private String username;
  private String email;
  private String password;
  private String organization;
  private List<String> roles;

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getOrganization() {
    return organization;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void erasePassword() {
    this.password = null;
  }
}
