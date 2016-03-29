package jwt.mail;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import jwt.AbstractApplicationTest;
import jwt.domain.User;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import static com.icegreen.greenmail.util.GreenMailUtil.getBody;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@WebIntegrationTest(randomPort = true, value = {"spring.data.mongodb.database=local", "spring.profiles.active=prod"})
public class DefaultMailBoxTest extends AbstractApplicationTest {

  @Autowired
  private MailBox mailBox;

  @Rule
  public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.ALL);

  @Test
  public void testMailBox() throws Exception {
    User user = new User("userName", "test@example.org", "org", emptyList(), "hash");
    mailBox.sendInvitationMail(user);

    //we send async
    Thread.sleep(1500);

    MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
    String body = getBody(mimeMessage);

    assertTrue(body.contains("http://localhost:8080/invite?key=hash"));
    assertEquals(user.getEmail(), mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
  }

}