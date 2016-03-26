package jwt.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MailConfiguration {

  @Bean
  @Profile({"!dev"})
  public MailBox mailSender() {
    return new DefaultMailBox();
  }

  @Bean
  @Profile({"dev"})
  public MailBox mailSenderDev() {
    return new MockMailBox();
  }
}
