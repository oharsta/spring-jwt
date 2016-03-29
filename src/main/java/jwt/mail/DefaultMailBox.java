package jwt.mail;

import jwt.domain.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class DefaultMailBox implements MailBox {

  @Autowired
  private JavaMailSender mailSender;
  private final String baseUrl;

  public DefaultMailBox(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public void sendInvitationMail(User user) {
    try {
      sendMail("mail/invite.html", "AcceptInvitation StoreWise", user.getEmail(), singletonMap("@@unique_invite_link@@", baseUrl + "/invite?key=" + user.getInvitationHash()));
    } catch (MessagingException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void sendMail(String templateName, String subject, String to, Map<String, String> variables) throws MessagingException, IOException {
    String html = IOUtils.toString(new ClassPathResource(templateName).getInputStream());
    for (Map.Entry<String, String> var : variables.entrySet()) {
      html = html.replaceAll(var.getKey(), var.getValue());
    }
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setSubject(subject);
    helper.setTo(to);
    setText(html, helper);
    helper.setFrom("info@zilverline.com");
    doSendMail(message);
  }

  protected void setText(String html, MimeMessageHelper helper) throws MessagingException {
    helper.setText(html, true);
  }

  protected void doSendMail(MimeMessage message) {
    new Thread(() -> mailSender.send(message)).start();
  }

}
