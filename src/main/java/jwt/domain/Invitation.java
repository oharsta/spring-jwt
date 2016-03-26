package jwt.domain;

import javax.validation.constraints.NotNull;

public class Invitation {

  private String invitationHash;
  private String password;

  public Invitation() {
  }

  public Invitation(String invitationHash, String password) {
    this.invitationHash = invitationHash;
    this.password = password;
  }

  @NotNull
  public String getInvitationHash() {
    return invitationHash;
  }
  @NotNull
  public String getPassword() {
    return password;
  }

}
