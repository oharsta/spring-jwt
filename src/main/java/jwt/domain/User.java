package jwt.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "users")
public class User {

  @Id
  private String id;

  @NotNull
  private String username;
  @NotNull
  private String email;
  private String password;
  @NotNull
  private String organization;
  private List<String> roles;
  private boolean active;
  @JsonIgnore
  private String invitationHash;

  public User() {
  }

  public User(String username, String email, String organization, List<String> roles) {
    this.username = username;
    this.email = email;
    this.organization = organization;
    this.roles = roles;
  }

  public User(String username, String email, String organization, List<String> roles, String invitationHash) {
    this.username = username;
    this.email = email;
    this.organization = organization;
    this.roles = roles;
    this.invitationHash = invitationHash;
  }

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

  public boolean isActive() {
    return active;
  }

  public void erasePassword() {
    this.password = null;
  }

  public void activate(String password) {
    this.active = true;
    this.password = password;
    this.invitationHash = null;
  }

  public String getInvitationHash() {
    return invitationHash;
  }

}
