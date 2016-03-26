package jwt.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class MailConfiguration {

  @Value("${base.url}")
  private String baseUrl;

  @Bean
  @Profile({"!dev"})
  public MailBox mailSenderProd() {
    return new DefaultMailBox(baseUrl);
  }

  @Bean
  @Profile({"dev"})
  @Primary
  public MailBox mailSenderDev() {
    return new MockMailBox(baseUrl);
  }
}
