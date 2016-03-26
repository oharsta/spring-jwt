package jwt.mail;

import jwt.domain.User;

import javax.mail.MessagingException;
import java.util.Map;

public interface MailBox {

  void sendInvitationMail(User user) ;

}
