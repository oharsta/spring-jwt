package jwt.domain;

import javax.validation.constraints.NotNull;

public class AcceptInvitation {
  @NotNull
  private String invitationHash;
  @NotNull
  private String password;

  public AcceptInvitation() {
  }

  public AcceptInvitation(String invitationHash, String password) {
    this.invitationHash = invitationHash;
    this.password = password;
  }


  public String getInvitationHash() {
    return invitationHash;
  }

  public String getPassword() {
    return password;
  }

}
