package jwt.mail;

import org.apache.commons.io.FileUtils;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

public class MockMailBox extends DefaultMailBox {

  public MockMailBox(String baseUrl) {
    super(baseUrl);
  }

  @Override
  protected void doSendMail(MimeMessage message) {
    //nope
  }

  @Override
  protected void setText(String html, MimeMessageHelper helper) throws MessagingException {
    try {
      String osName = System.getProperty("os.name").toLowerCase();
      if (osName.contains("mac os x")) {
        openInBrowser(html);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void openInBrowser(String text) throws IOException {
    File tempFile = File.createTempFile("javamail", ".html");
    FileUtils.writeStringToFile(tempFile, text);
    Runtime.getRuntime().exec("open " + tempFile.getAbsolutePath());
  }
}
